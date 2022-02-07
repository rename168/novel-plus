{
	"bookListUrl": "http://m.ibiquge.net/xclass/{catId}/{page}.html",
	"catIdRule": {
		"catId1": "1",
		"catId2": "2",
		"catId3": "3",
		"catId4": "4",
		"catId5": "6",
		"catId6": "5",
		"catId7": "7"
	},
	"bookIdPatten": "href=\"/(\\d+_\\d+)/\"",
	"pagePatten": "value=\"(\\d+)/\\d+\"",
	"totalPagePatten": "value=\"\\d+/(\\d+)\"",
	"bookDetailUrl": "http://m.ibiquge.net/{bookId}/",
	"bookNamePatten": "<span class=\"title\">([^/]+)</span>",
	"authorNamePatten": "<a href=\"/author/\\d+/\">([^/]+)</a>",
	"picUrlPatten": "<img src=\"([^>]+)\"\\s+onerror=\"this.src=",
	"picUrlPrefix": "http://m.ibiquge.net",
	"statusPatten": ">状态：([^/]+)</li>",
	"bookStatusRule": {
		"连载": 0,
		"完结": 1
	},
	"visitCountPatten": ">点击：(\\d+)</li>",
	"descStart": "<p class=\"review\">",
	"descEnd": "</p>",
	"bookIndexUrl": "http://www.ibiquge.net/{bookId}/",
	"bookIndexStart": "正文</dt>",
	"indexIdPatten": "<a\\s+style=\"\"\\s+href=\"/\\d+_\\d+/(\\d+)\\.html\">[^/]+</a>",
	"indexNamePatten": "<a\\s+style=\"\"\\s+href=\"/\\d+_\\d+/\\d+\\.html\">([^/]+)</a>",
	"bookContentUrl": "http://www.ibiquge.net/{bookId}/{indexId}.html",
	"contentStart": "</p>",
	"contentEnd": "<div align=\"center\">"
}