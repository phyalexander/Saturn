package utils


/** Provides access to a part of indexed sequence without copying.
 */
case class Slice[A](source: IndexedSeq[A], start: Int, end: Int) extends Iterable[A] {

    override def iterator: Iterator[A] = new SliceIterator(source, start, end)


    class SliceIterator(val source: IndexedSeq[A],
                        var car: Int, val end: Int) extends Iterator[A] {

        override def next(): A = {
            val elem = source(car)
            car += 1
            elem
        }

        override def hasNext: Boolean = car < end
    }
}


object Slice {

    def makeSlices[A](source: IndexedSeq[A], sliceNumber: Int): Iterator[Slice[A]] = {
        val size2 = source.size / sliceNumber
        val size1 = size2 + 1
        val part1 = source.size % sliceNumber
        val part2 = sliceNumber - part1
        val it1 = Iterator.tabulate(part1)(_ * size1).map(j => (j, j + size1))
        val it2 = Iterator.tabulate(part2)(_*size2 + part1*size1).map(j => (j, j + size2))
        it1.concat(it2).map(Slice(source, _, _))
    }
}




















//
