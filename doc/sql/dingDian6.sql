INSERT INTO crawl_source(
`source_name`,
`crawl_rule`,
`source_status`,
`create_time`,
`update_time` 
)
VALUES
(
'顶点6小说网',
'{\"bookListUrl\":\"https://m.dingdian6.com/category/{catId}_{page}.html\",\"catIdRule\":{\"catId1\":\"1\",\"catId2\":\"2\",\"catId3\":\"3\",\"catId4\":\"4\",\"catId5\":\"5\",\"catId6\":\"6\",\"catId7\":\"7\"},\"bookIdPatten\":\"href=\\\"/book(\\\\d+)/\\\"\",\"pagePatten\":\"value=\\\"(\\\\d+)/10\\\"\",\"totalPagePatten\":\"value=\\\"\\\\d+/(\\\\d+)\\\"\",\"bookDetailUrl\":\"https://m.dingdian6.com/book{bookId}\",\"bookNamePatten\":\"([^/]+)\",\"authorNamePatten\":\">作者：([^/]+)<\",\"picUrlPatten\":\"\\\"\\\\s+onerror=\\\"this.src=\",\"statusPatten\":\"状态：([^/]+)\",\"bookStatusRule\":{\"连载\":0,\"完成\":1},\"scorePatten\":\"(\\\\d+\\\\.\\\\d+)\",\"descStart\":\"\",\"upadateTimePatten\":\"更新：(\\\\d+-\\\\d+-\\\\d+)\",\"upadateTimeFormatPatten\":\"yyyy-MM-dd\",\"bookIndexUrl\":\"https://m.dingdian6.com/book{bookId}.html\",\"indexIdPatten\":\"[^/]+\",\"indexNamePatten\":\"([^/]+)\",\"bookContentUrl\":\"https://www.dingdian6.com/book{bookId}/{indexId}.html\",\"contentStart\":\"id=\\\"TXT\\\">\",\"contentEnd\":\"<script\"}',
1,
'2021-08-28 22:09:19',
'2021-08-28 22:09:19' 
);