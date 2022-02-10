{
	"bookListUrl": "https://m.x23us.us/ph/all{catId}_{page}.html",
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
	"bookDetailUrl": "https://m.x23us.us/book/{bookId}",
	"bookNamePatten": "<span class=\"title\">([^/]+)</span>",
	"authorNamePatten": "<a href=\"/author/[^/]+\">([^/]+)</a>",
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
	"bookIndexUrl": "https://m.x23us.us/book/{bookId}/all.html",
	"bookIndexStart": "直达页面底部</a></p>",
	"indexIdPatten": "<a href='/book/\\d+/(\\d+)\\.html'>[^/]+</a>",
	"indexNamePatten": "<a href='/book/\\d+/\\d+\\.html'>([^/]+)</a>",
	"bookContentUrl": "https://m.x23us.us/book/{bookId}/{indexId}.html",
	"contentStart": "<b>最新网址：m.x23us.us</b></div>",
	"contentEnd": "<div id=\"center_tip\">"
}