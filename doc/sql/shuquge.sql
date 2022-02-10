{
	"bookListUrl": "http://wap.shuquge.com/sort/{catId}/0_{page}.html",
	"catIdRule": {
		"catId1": "1",
		"catId2": "2",
		"catId3": "3",
		"catId4": "4",
		"catId5": "7",
		"catId6": "6",
		"catId7": "8"
	},
	"bookIdPatten": "href=\"/s/(\\d+)\\.html\"",
	"pagePatten": "第(\\d+)/\\d+页",
	"totalPagePatten": "第\\d+/(\\d+)页",
	"bookDetailUrl": "http://wap.shuquge.com/s/{bookId}.html",
	"bookNamePatten": "<a\\s+href=\"/s/\\d+\\.html\"><h2>([^/]+)</h2></a>",
	"authorNamePatten": "<p>作者：([^/]+)</p>",
	"picUrlPatten": "src=\"(https://www.shuquge.com/files/article/image/\\d+/\\d+/\\d+s\\.jpg)\"",
	"statusPatten": "<p>状态：([^/]+)</p>",
	"bookStatusRule": {
		"连载中": 0,
		"完本": 1
	},
	"descStart": "<div class=\"intro_info\">",
	"descEnd": "最新章节推荐地址",
	"bookIndexUrl": "http://www.shuquge.com/txt/{bookId}/index.html",
	"bookIndexStart": "<dt>《",
	"indexIdPatten": "<dd><a\\s+href=\"(\\d+)\\.html\">[^/]+</a></dd>",
	"indexNamePatten": "<dd><a\\s+href=\"\\d+\\.html\">([^/]+)</a></dd>",
	"bookContentUrl": "http://www.shuquge.com/txt/{bookId}/{indexId}.html",
	"contentStart": "<div id=\"content\" class=\"showtxt\">",
	"contentEnd": "https://www.shuquge.com"
}