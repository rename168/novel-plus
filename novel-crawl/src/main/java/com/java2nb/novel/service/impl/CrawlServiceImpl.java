package com.java2nb.novel.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.java2nb.novel.core.bean.PageBean;
import com.java2nb.novel.core.cache.CacheKey;
import com.java2nb.novel.core.cache.CacheService;
import com.java2nb.novel.core.crawl.CrawlParser;
import com.java2nb.novel.core.crawl.RuleBean;
import com.java2nb.novel.core.enums.ResponseStatus;
import com.java2nb.novel.core.exception.BusinessException;
import com.java2nb.novel.core.utils.BeanUtil;
import com.java2nb.novel.core.utils.IdWorker;
import com.java2nb.novel.core.utils.SpringUtil;
import com.java2nb.novel.core.utils.StringUtil;
import com.java2nb.novel.core.utils.ThreadUtil;
import com.java2nb.novel.entity.Book;
import com.java2nb.novel.entity.BookContent;
import com.java2nb.novel.entity.BookIndex;
import com.java2nb.novel.entity.CrawlSingleTask;
import com.java2nb.novel.entity.CrawlSource;
import com.java2nb.novel.mapper.CrawlSingleTaskDynamicSqlSupport;
import com.java2nb.novel.mapper.CrawlSingleTaskMapper;
import com.java2nb.novel.mapper.CrawlSourceDynamicSqlSupport;
import com.java2nb.novel.mapper.CrawlSourceMapper;
import com.java2nb.novel.service.BookService;
import com.java2nb.novel.service.CrawlService;
import com.java2nb.novel.vo.CrawlSingleTaskVO;
import com.java2nb.novel.vo.CrawlSourceVO;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.java2nb.novel.core.utils.HttpUtil.getByHttpClientWithChrome;
import static com.java2nb.novel.mapper.CrawlSourceDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.select.SelectDSL.select;

/**
 * @author Administrator
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CrawlServiceImpl implements CrawlService {

    private final CrawlSourceMapper crawlSourceMapper;

    private final CrawlSingleTaskMapper crawlSingleTaskMapper;

    private final BookService bookService;

    private final CacheService cacheService;

    @Override
    public void addCrawlSource(CrawlSource source) {
        Date currentDate = new Date();
        source.setCreateTime(currentDate);
        source.setUpdateTime(currentDate);
        crawlSourceMapper.insertSelective(source);

    }

    @Override
    public PageBean<CrawlSource> listCrawlByPage(int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        SelectStatementProvider render = select(id, sourceName, sourceStatus, createTime, updateTime)
                .from(crawlSource)
                .orderBy(updateTime)
                .build()
                .render(RenderingStrategies.MYBATIS3);
        List<CrawlSource> crawlSources = crawlSourceMapper.selectMany(render);
        PageBean<CrawlSource> pageBean = new PageBean<>(crawlSources);
        pageBean.setList(BeanUtil.copyList(crawlSources, CrawlSourceVO.class));
        return pageBean;
    }

    /**
     * 读取filePath的文件，将文件中的数据按照行读取到String数组中
     *
     * @param filePath 文件的路径
     * @return 文件中一行一行的数据
     */
    public static String readToString(String filePath) {
        File file = new File(filePath);
        Long filelength = file.length(); // 获取文件长度
        char[] filecontent = new char[filelength.intValue()];
        // CharBuffer filecontent = new CharBuffer ();
        try {
            FileInputStream in = new FileInputStream(file);
            BufferedReader read = new BufferedReader(new InputStreamReader(in, "utf-8"));
            read.read(filecontent);
            // in.read(filecontent, "utf-8");
            read.close();
            in.close();
            read = null;
            in = null;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String re = new String(filecontent);

        return re;// 返回文件内容,默认编码
    }

    // String baseDir = "D:\\work\\xiaoshuo\\novelPrivate\\down_kenshu\\javaTest";
    @Value("${txt.save.path}")
    private String fileSavePath;

    ArrayList<String> filterList = new ArrayList<String>() {

    };

    private ArrayList<String> findContent(Iterator it) {
        ArrayList<String> lines = new ArrayList<>();

        while (it.hasNext()) {
            String item = (String) it.next();
            if (item.equals("------------")) {
                break;
            } else {
                lines.add(item);
            }
        }
        return lines;
    }

    private String findChapterName(Iterator it) {
        String name;
        while (it.hasNext()) {
            String item = (String) it.next();
            if (item.equals("------------")) {
                continue;
            }
            if (item.trim().length() > 0) {
                name = item;
                return name;
            }
        }
        return "";
    }

    private String concatContent(ArrayList<String> chapterContentList) {
        StringBuilder re = new StringBuilder();
        String tmpItem;
        // re.append("\r\n");
        for (int i = 0; i < chapterContentList.size(); i++) {
            tmpItem = chapterContentList.get(i);
            re.append(tmpItem);
            re.append("\r\n");
        }

        return re.toString();
    }

    public int checkCatId(String catName, int type) {
        if (catName == null)
            return type;

        if (catName.contains("玄幻") || catName.contains("修真")) {
            return 1;
        } else if (catName.contains("武侠") || catName.contains("仙侠")) {
            return 2;
        } else if (catName.contains("都市") || catName.contains("言情")) {
            return 3;
        } else if (catName.contains("历史") || catName.contains("军事")) {
            return 4;
        } else if (catName.contains("科幻") || catName.contains("灵异")) {
            return 5;
        } else if (catName.contains("网游") || catName.contains("竞技")) {
            return 6;
        } else if (catName.contains("女生") || catName.contains("女频")) {
            return 7;
        }

        return 1;
    }

    public void parseLocalBook(TxtBookData bookItem) {

        Date currentDate = new Date();
        String baseDir = fileSavePath + "/../javaTest";

        String filePath = String.format("%s/%s/%s", baseDir, "txtFile", bookItem.title);

        File file = new File(filePath);

        if (!file.exists()) {
            // log.error("file not fond: {} ", bookItem.title);
            return;
        }
        if (bookService.queryIsExistByBookNameAndAuthorName(bookItem.title, bookItem.author)) {
            // log.error(" data base exist: {} ", bookItem.title);

            return;
        }

        ArrayList<String> lines = new ArrayList<>();
        // ArrayList<ChapterData> chapterList = new ArrayList<>();
        try {
            // in = new BufferedReader(new FileReader(filePath));
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "utf-8"));
            String str;
            while ((str = reader.readLine()) != null) {
                lines.add(str);
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // String content = readToString(filePath);

        // BufferedReader in;

        Book book = new Book();
        List<BookIndex> bookIndexList = new ArrayList<BookIndex>();
        List<BookContent> bookContentList = new ArrayList<BookContent>();

        book.setAuthorName(bookItem.author);
        book.setBookName(bookItem.title);
        book.setId(Long.parseLong(bookItem.code));
        book.setCatId(checkCatId(bookItem.catName, bookItem.type + 1));
        if (book.getCatId() == 7) {
            // 女频
            book.setWorkDirection((byte) 1);
        } else {
            // 男频
            book.setWorkDirection((byte) 0);
        }

        book.setCatName(bookService.queryCatNameByCatId(book.getCatId()));

        book.setCrawlBookId(bookItem.code);
        book.setCrawlSourceId(0);
        book.setCrawlLastTime(currentDate);
        if (bookItem.imgUrl != null)
            book.setPicUrl(bookItem.imgUrl);
        else {
            book.setPicUrl("-");
        }
        if (bookItem.desc != null)
            if (bookItem.desc.length() > 1900) {
                book.setBookDesc(bookItem.desc.substring(0, 1900));
                log.error(" desc too long !  {} ", bookItem.desc);
            } else
                book.setBookDesc(bookItem.desc);
        else {

            book.setBookDesc("-");
        }

        book.setScore(1f);
        book.setVisitCount(1l);
        if (bookItem.complete != null) {
            if (bookItem.complete.equals("连载")) {
                book.setBookStatus((byte) 0);
            } else {
                book.setBookStatus((byte) 1);
            }
        } else {
            book.setBookStatus((byte) 0);
        }

        book.setLastIndexUpdateTime(currentDate);

        int totalWordCount = 0;

        Iterator<String> it = lines.iterator();
        int indexNum = 0;
        while (it.hasNext()) {
            // String item = (String) it.next();
            // if (item.equals("------------")) {
            String chapterName = findChapterName(it);
            ArrayList<String> chapterContentList = findContent(it);
            String content = concatContent(chapterContentList);

            if (chapterName.length() > 80) {
                log.error(" 章节名错误  书名：{}    当前章: {}", bookItem.title,
                        chapterName);
                // chapterName = chapterName.substring(0, 50);

                return;
            }
            // ChapterData data = new ChapterData();
            // data.chapterName = chapterName;
            // data.content = chapterContent;

            // chapterList.add(data);
            if (chapterName.trim().equals("正文") ||
                    chapterName.trim().equals("正文卷") ||
                    chapterContentList.size() <= 2) {
                if (chapterContentList.size() <= 2) {

                    log.info(" 正文卷过滤 content len{}", chapterContentList.size());
                } else {
                    log.info(" 正文卷异常 chapter: {}  content len{}", chapterName, chapterContentList.size());
                }
            } else {
                log.info(" fond chapter: {}  content len{}", chapterName, chapterContentList.size());

                BookIndex bookIndex = new BookIndex();
                bookIndex.setIndexName(chapterName);
                bookIndex.setIndexNum(indexNum);

                int wordCount = StringUtil.getStrValidWordCount(content);
                bookIndex.setWordCount(wordCount);

                BookContent bookContent = new BookContent();
                bookContent.setContent(content);

                bookContentList.add(bookContent);
                bookIndexList.add(bookIndex);

                // Long indexId = CrawlParser.idWorker.nextId();
                Long indexId = book.getId() * 100000l + indexNum;

                bookIndex.setId(indexId);
                bookIndex.setBookId(book.getId());

                bookIndex.setCreateTime(currentDate);
                bookContent.setIndexId(indexId);

                if (content.contains("正在手打中")) {
                    log.error(" wrong content  正在手打中getBookName{}  chapterName {}   indexId{}", book.getBookName(),
                            chapterName, indexId);
                    bookIndex.setHaswrong((byte) 1);
                } else {
                    bookIndex.setHaswrong((byte) 0);
                }

                // 计算总字数
                totalWordCount += wordCount;

                indexNum++;
            }
            // System.out.println(" fond chapter: {}" + chapterName);
            // }
        }

        if (bookIndexList.size() > 0) {

            book.setWordCount(totalWordCount);
            book.setUpdateTime(currentDate);
            book.setLastIndexId(bookIndexList.get(bookIndexList.size() - 1).getId());
            book.setLastIndexName(bookIndexList.get(bookIndexList.size() - 1).getIndexName());
            try {
                bookService.saveBookAndIndexAndContent(book, bookIndexList, bookContentList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 加载txt
     *
     * @param sourceId     爬虫源ID
     * @param sourceStatus 状态，0关闭，1开启
     */
    @SneakyThrows
    @Override
    public void loadTxt() {

        String path2 = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();

        log.info(" ---------loadTxt  path2{}  ", path2);
        String baseDir = fileSavePath + "/../javaTest";
        String content = readToString(baseDir + "/bookListJson.json");
        // log.info(" ---------loadTxt content {} ", content);

        // TxtBookData[] tableNames = new TxtBookData[] {};
        ArrayList<TxtBookData> txtList = (ArrayList<TxtBookData>) JSON.parseArray(content, TxtBookData.class);

        for (int i = 0; i < txtList.size(); i++) {
            TxtBookData bookItem = txtList.get(i);
            // log.info(" ---------TxtBookData {} ", bookItem);
            parseLocalBook(bookItem);
        }

        // jarr.toArray(tableNames);
    }

    @SneakyThrows
    @Override
    public void openOrCloseCrawl(Integer sourceId, Byte sourceStatus) {

        // 判断是开启还是关闭，如果是关闭，则修改数据库状态后获取该爬虫正在运行的线程集合并全部停止
        // 如果是开启，先查询数据库中状态，判断该爬虫源是否还在运行，如果在运行，则忽略，
        // 如果没有则修改数据库状态，并启动线程爬取小说数据加入到runningCrawlThread中
        if (sourceStatus == (byte) 0) {
            // 关闭,直接修改数据库状态，并直接修改数据库状态后获取该爬虫正在运行的线程集合全部停止
            SpringUtil.getBean(CrawlService.class).updateCrawlSourceStatus(sourceId, sourceStatus);
            Set<Long> runningCrawlThreadId = (Set<Long>) cacheService
                    .getObject(CacheKey.RUNNING_CRAWL_THREAD_KEY_PREFIX + sourceId);
            if (runningCrawlThreadId != null) {
                for (Long ThreadId : runningCrawlThreadId) {
                    Thread thread = ThreadUtil.findThread(ThreadId);
                    if (thread != null && thread.isAlive()) {
                        thread.interrupt();
                    }
                }
            }

        } else {
            // 开启
            // 查询爬虫源状态和规则
            CrawlSource source = queryCrawlSource(sourceId);
            Byte realSourceStatus = source.getSourceStatus();

            int maxCate = 8;
            if (realSourceStatus == (byte) 0) {
                // 该爬虫源已经停止运行了,修改数据库状态，并启动线程爬取小说数据加入到runningCrawlThread中
                SpringUtil.getBean(CrawlService.class).updateCrawlSourceStatus(sourceId, sourceStatus);
                RuleBean ruleBean = new ObjectMapper().readValue(source.getCrawlRule(), RuleBean.class);
                Integer threadNum = ruleBean.getThreadNum();
                if (threadNum == null) {
                    threadNum = 8;
                } else if (threadNum > 8) {
                    threadNum = 8;
                }

                Set<Long> threadIds = new HashSet<>();

                int perThreadCount = (int) (maxCate / threadNum);
                threadNum = (int) (maxCate / perThreadCount);
                if (threadNum < 8) {
                    log.error(" threadNum{}  perThreadCount{} ", threadNum, perThreadCount);
                }

                // for(int i = 0 ; i< threadNum; i++ ){
                // int begin = i *perThreadCount;
                // Thread thread = new Thread(() ->{
                // for( int j = begin ; j<begin+perThreadCount&& j<maxCate ; j++ ){
                // final int catId = j;
                // CrawlServiceImpl.this.parseBookList(catId, ruleBean, sourceId);
                // }
                // } );
                // thread.start();
                // //thread加入到监控缓存中
                // threadIds.add(thread.getId());
                // }

                // 按分类开始爬虫解析任务
                for (int i = 1; i < 8; i++) {
                    final int catId = i;
                    Thread thread = new Thread(() -> CrawlServiceImpl.this.parseBookList(catId, ruleBean, sourceId));
                    thread.start();
                    // thread加入到监控缓存中
                    threadIds.add(thread.getId());
                }
                cacheService.setObject(CacheKey.RUNNING_CRAWL_THREAD_KEY_PREFIX + sourceId, threadIds);

            }

        }

    }

    @Override
    public CrawlSource queryCrawlSource(Integer sourceId) {
        SelectStatementProvider render = select(CrawlSourceDynamicSqlSupport.sourceStatus,
                CrawlSourceDynamicSqlSupport.crawlRule)
                .from(crawlSource)
                .where(id, isEqualTo(sourceId))
                .build()
                .render(RenderingStrategies.MYBATIS3);
        return crawlSourceMapper.selectMany(render).get(0);
    }

    @Override
    public void addCrawlSingleTask(CrawlSingleTask singleTask) {

        if (bookService.queryIsExistByBookNameAndAuthorName(singleTask.getBookName(), singleTask.getAuthorName())) {
            throw new BusinessException(ResponseStatus.BOOK_EXISTS);

        }
        singleTask.setCreateTime(new Date());
        crawlSingleTaskMapper.insertSelective(singleTask);

    }

    @Override
    public PageBean<CrawlSingleTask> listCrawlSingleTaskByPage(int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        SelectStatementProvider render = select(CrawlSingleTaskDynamicSqlSupport.crawlSingleTask.allColumns())
                .from(CrawlSingleTaskDynamicSqlSupport.crawlSingleTask)
                .orderBy(CrawlSingleTaskDynamicSqlSupport.createTime.descending())
                .build()
                .render(RenderingStrategies.MYBATIS3);
        List<CrawlSingleTask> crawlSingleTasks = crawlSingleTaskMapper.selectMany(render);
        PageBean<CrawlSingleTask> pageBean = new PageBean<>(crawlSingleTasks);
        pageBean.setList(BeanUtil.copyList(crawlSingleTasks, CrawlSingleTaskVO.class));
        return pageBean;
    }

    @Override
    public void delCrawlSingleTask(Long id) {
        crawlSingleTaskMapper.deleteByPrimaryKey(id);
    }

    @Override
    public CrawlSingleTask getCrawlSingleTask() {

        List<CrawlSingleTask> list = crawlSingleTaskMapper
                .selectMany(select(CrawlSingleTaskDynamicSqlSupport.crawlSingleTask.allColumns())
                        .from(CrawlSingleTaskDynamicSqlSupport.crawlSingleTask)
                        .where(CrawlSingleTaskDynamicSqlSupport.taskStatus, isEqualTo((byte) 2))
                        .orderBy(CrawlSingleTaskDynamicSqlSupport.createTime)
                        .limit(1)
                        .build()
                        .render(RenderingStrategies.MYBATIS3));

        return list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public void updateCrawlSingleTask(CrawlSingleTask task, Byte status) {
        byte excCount = task.getExcCount();
        excCount += 1;
        task.setExcCount(excCount);
        if (status == 1 || excCount == 5) {
            // 当采集成功或者采集次数等于5，则更新采集最终状态，并停止采集
            task.setTaskStatus(status);
        }
        crawlSingleTaskMapper.updateByPrimaryKeySelective(task);

    }

    /**
     * 解析分类列表
     */
    @Override
    public void parseBookList(int catId, RuleBean ruleBean, Integer sourceId) {

        // 当前页码1
        int page = 1;
        int totalPage = page;

        while (page <= totalPage) {

            try {

                if (StringUtils.isNotBlank(ruleBean.getCatIdRule().get("catId" + catId))) {
                    // 拼接分类URL
                    String catBookListUrl = ruleBean.getBookListUrl()
                            .replace("{catId}", ruleBean.getCatIdRule().get("catId" + catId))
                            .replace("{page}", page + "");

                    String bookListHtml = getByHttpClientWithChrome(catBookListUrl);
                    if (bookListHtml != null) {
                        Pattern bookIdPatten = Pattern.compile(ruleBean.getBookIdPatten());
                        Matcher bookIdMatcher = bookIdPatten.matcher(bookListHtml);
                        boolean isFindBookId = bookIdMatcher.find();
                        while (isFindBookId) {
                            try {
                                // 1.阻塞过程（使用了 sleep,同步锁的 wait,socket 中的 receiver,accept 等方法时）
                                // 捕获中断异常InterruptedException来退出线程。
                                // 2.非阻塞过程中通过判断中断标志来退出线程。
                                if (Thread.currentThread().isInterrupted()) {
                                    return;
                                }

                                String bookId = bookIdMatcher.group(1);
                                parseBookAndSave(catId, ruleBean, sourceId, bookId);
                            } catch (Exception e) {
                                log.error(e.getMessage(), e);
                            }

                            isFindBookId = bookIdMatcher.find();
                        }

                        Pattern totalPagePatten = Pattern.compile(ruleBean.getTotalPagePatten());
                        Matcher totalPageMatcher = totalPagePatten.matcher(bookListHtml);
                        boolean isFindTotalPage = totalPageMatcher.find();
                        if (isFindTotalPage) {

                            totalPage = Integer.parseInt(totalPageMatcher.group(1));

                        }

                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

            page += 1;
        }

    }

    @Override
    public boolean parseBookAndSave(int catId, RuleBean ruleBean, Integer sourceId, String bookId) {

        final AtomicBoolean parseResult = new AtomicBoolean(false);

        CrawlParser.parseBook(ruleBean, bookId, book -> {
            if (book.getBookName() == null || book.getAuthorName() == null) {
                log.error(" getBookName  or getAuthorName   not find  {}  {}", book.getBookName(),
                        book.getAuthorName());
                new Throwable().printStackTrace();
                return;
            } else {
            }
            // 这里只做新书入库，查询是否存在这本书
            Book existBook = bookService.queryBookByBookNameAndAuthorName(book.getBookName(), book.getAuthorName());
            // 如果该小说不存在，则可以解析入库，但是标记该小说正在入库，30分钟之后才允许再次入库
            if (existBook == null) {
                // 没有该书，可以入库
                book.setCatId(catId);
                // 根据分类ID查询分类
                book.setCatName(bookService.queryCatNameByCatId(catId));
                if (catId == 7) {
                    // 女频
                    book.setWorkDirection((byte) 1);
                } else {
                    // 男频
                    book.setWorkDirection((byte) 0);
                }
                book.setCrawlBookId(bookId);
                book.setCrawlSourceId(sourceId);
                book.setCrawlLastTime(new Date());
                book.setId(new IdWorker().nextId());
                // 解析章节目录
                CrawlParser.parseBookIndexAndContent(bookId, book, ruleBean, new HashMap<>(0), chapter -> {
                    bookService.saveBookAndIndexAndContent(book, chapter.getBookIndexList(),
                            chapter.getBookContentList());
                });

            } else {
                // 只更新书籍的爬虫相关字段
                // bookService.updateCrawlProperties(existBook.getId(), sourceId, bookId);
                // 其他网站趴的， 难道要更新？
                log.info(" already exist book: {} anthor {} ", book.getBookName(), book.getAuthorName());

            }
            parseResult.set(true);
        });

        return parseResult.get();

    }

    @Override
    public void updateCrawlSourceStatus(Integer sourceId, Byte sourceStatus) {
        CrawlSource source = new CrawlSource();
        source.setId(sourceId);
        source.setSourceStatus(sourceStatus);
        crawlSourceMapper.updateByPrimaryKeySelective(source);
    }

    @Override
    public List<CrawlSource> queryCrawlSourceByStatus(Byte sourceStatus) {
        SelectStatementProvider render = select(CrawlSourceDynamicSqlSupport.id,
                CrawlSourceDynamicSqlSupport.sourceStatus, CrawlSourceDynamicSqlSupport.crawlRule)
                .from(crawlSource)
                .where(CrawlSourceDynamicSqlSupport.sourceStatus, isEqualTo(sourceStatus))
                .build()
                .render(RenderingStrategies.MYBATIS3);
        return crawlSourceMapper.selectMany(render);
    }
}
