{
	"bookListUrl": "https://m.bookb.net/waptop/{catId}_{page}.html",
	
	"catIdRule": {
		"catId1": "1",
		"catId2": "2",
		"catId3": "3",
		"catId4": "4",
		"catId5": "5",
		"catId6": "6",
		"catId7": "7"
	},
	"bookIdPatten": "href=\"/book/(\\d+)/\"",
	"pagePatten": "value=\"(\\d+)/10\"",
	"totalPagePatten": "value=\"\\d+/(\\d+)\"",
	"bookDetailUrl": "https://m.bookb.net/book/{bookId}",
	"bookNamePatten": "<span class=\"title\">([^/]+)</span>",
	"authorNamePatten": ">作者：([^/]+)<",
	"picUrlPatten": "<img src=\"([^>]+)\"\\s+onerror=\"this.src=",
	"statusPatten": "状态：([^/]+)",
	"bookStatusRule": {
		"连载": 0,
		"完成": 1
	},
	"scorePatten": "(\\d+\\.\\d+)",
	"descStart": "<p class=\"review\">",
	"descEnd": "</p>",
	"upadateTimePatten": "更新：(\\d+-\\d+-\\d+)",
	"upadateTimeFormatPatten": "yyyy-MM-dd",
	"bookIndexUrl": "https://m.bookb.net/book/{bookId}.html",
	"bookIndexStart": "直达页面底部</a></p>",
	"indexIdPatten": "<a\\s+style=\"\"\\s+href=\"/book/\\d+/(\\d+)\\.html\">[^/]+</a>",
	"indexNamePatten": "<a\\s+style=\"\"\\s+href=\"/book/\\d+/\\d+\\.html\">([^/]+)</a>",
	"bookContentUrl": "https://www.bookb.net/book/{bookId}/{indexId}.html",
	"contentStart": "id=\"TXT\">",
	"contentEnd": "<script"
}