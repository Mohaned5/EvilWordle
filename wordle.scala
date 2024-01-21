import io.Source
import scala.util._

object M2 { 

import io.Source
import scala.util._

/* Takes an URL-string as argument and requests the corresponding file. 
Returns the word list appropriately broken up into lines. */
def get_wordle_list(url: String) : List[String] = {
    Try(Source.fromURL(url)("ISO-8859-1").mkString.split("\n").toList).
        getOrElse {Nil}
}


// some starting URLs for the crawler
val secrets = get_wordle_list("https://nms.kcl.ac.uk/christian.urban/wordle.txt")

/* Removes n occurrences of an element from a list (if this element is less than
n times present, then remove all occurrences).*/
def removeN[A](xs: List[A], elem: A, n: Int) : List[A] = {
    xs match{
        case head :: rest=> 
            if (head == elem && n > 0) {
                removeN(rest, elem, n - 1)
            }
            else{
                head :: removeN(rest, elem, n)
            }
        case _=>xs
    }
}

abstract class Tip
case object Absent extends Tip
case object Present extends Tip
case object Correct extends Tip

/* Finds all letters in the secret that are not in the word*/
def pool(secret: String, word: String): List[Char] = {
  (secret.toList, word.toList) match {
    case (s, w) if s.isEmpty || w.isEmpty => Nil
    case (sHead :: sTail, wHead :: wTail) =>
        if (sHead == wHead){
            pool(sTail.mkString, wTail.mkString)
        }
        else {
            sHead :: pool(sTail.mkString, wTail.mkString)
        }
    }
}

/* Checks if each character in the word is Correct, Present or Absent and returns a list 
according to this */
def aux(secret: List[Char], word: List[Char], pool: List[Char]) : List[Tip] = {
    (secret.toList, word.toList) match {
        case (s, w) if s.isEmpty || w.isEmpty => Nil
        case (sHead :: sTail, wHead :: wTail) =>
            if (sHead == wHead){
                Correct :: aux(sTail, wTail, pool)
            }
            else if (sHead != wHead && pool.contains(wHead)){
                val index = pool.indexOf(wHead)
                val newList = pool.take(index) ::: pool.drop(index + 1)
                Present :: aux(sTail, wTail, newList)
            }
            else {
                Absent :: aux(sTail, wTail, pool)
            }
    }
}

/* Compare word to the secret word and return a list which has either
Correct, Present or Absent for each index of the word depending on wordle rules */
def score(secret: String, word: String) : List[Tip] = {
    aux(secret.toList, word.toList, pool(secret, word))
}

def eval(t: Tip) : Int = {
    t match{
        case Correct=>10
        case Present=>1
        case Absent=>0
    }
}

def iscore(secret: String, word: String) : Int = {
    score(secret, word).map(eval).sum
}

/* Calculates the score of each secret and returns words with lowest
score */
def lowest(secrets: List[String], word: String, current: Int, acc: List[String]) : List[String] = {
    secrets match {
        case head :: rest =>
            val score = iscore(head, word)
            if (score < current){
                lowest(rest, word, score, List(head))
            }
            else if (score == current){
                lowest(rest, word, current, acc :+ head)
            }
            else{
                lowest(rest, word, current, acc)
            }
        case _=>acc

    }
}

def evil(secrets: List[String], word: String) : List[String] = {
    lowest(secrets, word, Int.MaxValue, List())
}

/* Calculates the frequency of each character to find the characters
which appear the least in all words */
def frequencies(secrets: List[String]) : Map[Char, Double] = {
    val frequencyMap = secrets.mkString.groupBy(_.toLower).mapValues(_.length.toDouble)
    frequencyMap.mapValues(freq => 1 - (freq / frequencyMap.values.sum)).toMap
}

/* Generates a rank by summing the frequency of each letter in the word */
def rank(frqs: Map[Char, Double], s: String) : Double = {
    s.toList match {
        case char :: rest=>
            frqs(char) + rank(frqs, rest.mkString)
        case Nil => 0.0
    }
}

/* Selects the most evil words according to frequency and the users first guess */
def ranked_evil(secrets: List[String], word: String): List[String] = {
    val frequenciesMap = frequencies(secrets)
    val evils = evil(secrets, word)


    evils.foldLeft(List.empty[String] -> Double.MinValue) {
        case ((maxWords, maxRank), evilWord) =>
        val currentRank = rank(frequenciesMap, evilWord)

        if (currentRank > maxRank) {
            List(evilWord) -> currentRank
        } else if (currentRank == maxRank) {
            (evilWord :: maxWords, maxRank)
        } else {
            maxWords -> maxRank
        }
    }._1.reverse
}


}
