package com.mrt.myrealtrip

object WordCount {
    fun sort(content: String): List<String> {
        val keyword = mutableListOf<String>()
        val allKeyword = phrase(content)
        val firstSortedKeyword = allKeyword.toList().sortedWith(compareBy({it.first}))
        val secondSortedKeyword = firstSortedKeyword.sortedWith(
            compareByDescending { it.second })
        var count = 0

        if(content.equals("신문사의 오류로 읽어오지 못했습니다.")) {
            keyword.add("키워드")
            keyword.add("분석")
            keyword.add("실패")
            return keyword
        }

        if(secondSortedKeyword.size > 2) {
            while (keyword.size < 3 && secondSortedKeyword.size > count) {
                if (secondSortedKeyword[count].first.length >= 2) {
                    keyword.add(secondSortedKeyword[count].first)
                }
                count++
            }
            if(keyword.size < 3) {
                keyword.clear()
                keyword.add("키워드")
                keyword.add("분석")
                keyword.add("실패")
            }
        } else {
            keyword.add("키워드")
            keyword.add("분석")
            keyword.add("실패")
        }

        return keyword
    }

    fun phrase(phrase: String): Map<String, Int> {
        return toWords(phrase).
            groupBy { it }.
            mapValues({ it.value.size })
    }

    private fun toWords(phrase: String): List<String> {
        return phrase.
            replace(Regex("[^\\w]"), " ").trim().
            split(Regex("\\s+"))
    }
}