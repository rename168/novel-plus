INSERT INTO `crawl_source` (
`id`,
`source_name`,
`crawl_rule`,
`source_status`,
`create_time`,
`update_time` 
)
VALUES
(
11,
'笔趣阁U',
'{\n\"bookListUrl\": \"http://m.biqugeu.com/book/lhb/{catId}/{page}.html\",\n\"catIdRule\": {\n\"catId1\": \"1\",\n\"catId2\": \"2\",\n\"catId3\": \"3\",\n\"catId4\": \"4\",\n\"catId5\": \"5\",\n\"catId6\": \"6\",\n\"catId7\": \"7\"\n},\n\"bookIdPatten\": \"href=\\\"/book/(\\\\d+)/\\\"\",\n\"pagePatten\": \"value=\\\"(\\\\d+)/\\\\d+\\\"\",\n\"totalPagePatten\": \"value=\\\"\\\\d+/(\\\\d+)\\\"\",\n\"bookDetailUrl\": \"http://m.biqugeu.com/book/{bookId}/\",\n\"bookNamePatten\": \"([^/]+)\",\n\"authorNamePatten\": \"作者：([^/]+)\",\n\"picUrlPatten\": \"]+)\\\"\\\\s+onerror=\\\"this.src=\",\n\"statusPatten\": \"状态：([^/]+)\",\n\"bookStatusRule\": {\n\"连载\": 0,\n\"完成\": 1\n},\n\"scorePatten\": \"(\\\\d+\\\\.\\\\d+)\",\n\"descStart\": \"\",\n\"descEnd\": \"\",\n\"upadateTimePatten\": \"更新：(\\\\d+-\\\\d+-\\\\d+\\\\s+\\\\d+:\\\\d+:\\\\d+)\",\n\"upadateTimeFormatPatten\": \"yyyy-MM-dd HH:mm:ss\",\n\"bookIndexUrl\": \"http://m.biqugeu.com/book/{bookId}/all.html\",\n\"indexIdPatten\": \"[^/]+\",\n\"indexNamePatten\": \"([^/]+)\",\n\"bookContentUrl\": \"http://www.biqugeu.com/{cal_1_1_3}_{bookId}/{indexId}.html\",\n\"contentStart\": \"id=\\\"content\\\">\",\n\"contentEnd\": \"<script>\"\n}',
0,
'2020-12-10 18:04:33',
'2020-12-10 18:04:33' 
);
