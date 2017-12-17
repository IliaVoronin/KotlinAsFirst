@file:Suppress("UNUSED_PARAMETER", "unused")
package lesson7.task1


/**
 * Ячейка матрицы: row = ряд, column = колонка
 */
data class Cell(val row: Int, val column: Int)

/**
 * Интерфейс, описывающий возможности матрицы. E = тип элемента матрицы
 */
interface Matrix<E> {
    /** Высота */
    val height: Int

    /** Ширина */
    val width: Int

    /**
     * Доступ к ячейке.
     * Методы могут бросить исключение, если ячейка не существует или пуста
     */
    operator fun get(row: Int, column: Int): E
    operator fun get(cell: Cell): E

    /**
     * Запись в ячейку.
     * Методы могут бросить исключение, если ячейка не существует
     */
    operator fun set(row: Int, column: Int, value: E)
    operator fun set(cell: Cell, value: E)
}

/**
 * Простая
 *
 * Метод для создания матрицы, должен вернуть РЕАЛИЗАЦИЮ Matrix<E>.
 * height = высота, width = ширина, e = чем заполнить элементы.
 * Бросить исключение IllegalArgumentException, если height или width <= 0.
 */
fun <E> createMatrix(height: Int, width: Int, e: E): Matrix<E> {
    if ((height == 0) || (width == 0)) throw IllegalArgumentException()
    return MatrixImpl(height, width, e)
}

/**
 * Средняя сложность
 *
 * Реализация интерфейса "матрица"
 */
class MatrixImpl<E>(override val height: Int, override val width: Int, e: E) : Matrix<E> {
    private val newList = mutableListOf<E>()

    init {
        for (i in 0 until width * height) {
            newList.add(e)
        }
    }


    override fun get(row: Int, column: Int): E = newList[column + width * row]

    override fun get(cell: Cell): E = get(cell.row, cell.column)

    override fun set(row: Int, column: Int, value: E) {
        newList[width * row + column] = value
    }

    override fun set(cell: Cell, value: E) = set(cell.row, cell.column, value)


    override fun equals(other: Any?) = other is MatrixImpl<*> && height == other.height && width == other.width && newList == other.newList


    override fun toString(): String {

        val newStingBuilder = StringBuilder()
        newStingBuilder.append("[ \n")

        for (r in 0 until height) {
            newStingBuilder.append("[ ")
            for (c in 0 until width) {
                var t = " "
                if (c == width - 1) t = "\t"
                val element = this[r, c]
                newStingBuilder.append("$element $t")
            }
            newStingBuilder.append("]")
            newStingBuilder.append("\n")
        }
        newStingBuilder.append("]")
        return "$newStingBuilder"
    }

    override fun hashCode(): Int {
        var res = height
        res = 31 * res + width
        res = 31 * res + newList.hashCode()
        return res
    }
}