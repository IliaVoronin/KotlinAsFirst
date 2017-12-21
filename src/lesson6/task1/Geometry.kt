@file:Suppress("UNUSED_PARAMETER")

package lesson6.task1

import lesson1.task1.sqr
import lesson2.task2.pointInsideCircle
import java.lang.Math.PI
import java.lang.Math.atan
import kotlin.concurrent.fixedRateTimer

/**
 * Точка на плоскости
 */
data class Point(val x: Double, val y: Double) {
    /**
     * Пример
     *
     * Рассчитать (по известной формуле) расстояние между двумя точками
     */
    fun distance(other: Point): Double = Math.sqrt(sqr(x - other.x) + sqr(y - other.y))
}

/**
 * Треугольник, заданный тремя точками (a, b, c, см. constructor ниже).
 * Эти три точки хранятся в множестве points, их порядок не имеет значения.
 */
class Triangle private constructor(private val points: Set<Point>) {

    private val pointList = points.toList()

    val a: Point get() = pointList[0]

    val b: Point get() = pointList[1]

    val c: Point get() = pointList[2]

    constructor(a: Point, b: Point, c: Point) : this(linkedSetOf(a, b, c))

    /**
     * Пример: полупериметр
     */
    fun halfPerimeter() = (a.distance(b) + b.distance(c) + c.distance(a)) / 2.0

    /**
     * Пример: площадь
     */
    fun area(): Double {
        val p = halfPerimeter()
        return Math.sqrt(p * (p - a.distance(b)) * (p - b.distance(c)) * (p - c.distance(a)))
    }

    /**
     * Пример: треугольник содержит точку
     */
    fun contains(p: Point): Boolean {
        val abp = Triangle(a, b, p)
        val bcp = Triangle(b, c, p)
        val cap = Triangle(c, a, p)
        return abp.area() + bcp.area() + cap.area() <= area()
    }

    override fun equals(other: Any?) = other is Triangle && points == other.points

    override fun hashCode() = points.hashCode()

    override fun toString() = "Triangle(a = $a, b = $b, c = $c)"
}

/**
 * Окружность с заданным центром и радиусом
 */
data class Circle(val center: Point, val radius: Double) {
    /**
     * Простая
     *
     * Рассчитать расстояние между двумя окружностями.
     * Расстояние между непересекающимися окружностями рассчитывается как
     * расстояние между их центрами минус сумма их радиусов.
     * Расстояние между пересекающимися окружностями считать равным 0.0.
     */
    fun distance(other: Circle): Double {
        val sumOfRad = radius + other.radius
        val distance = center.distance(other.center)

        return if (distance < sumOfRad) 0.0 else distance - sumOfRad
    }

    /**
     * Тривиальная
     *
     * Вернуть true, если и только если окружность содержит данную точку НА себе или ВНУТРИ себя
     */
    fun contains(p: Point): Boolean = radius >= center.distance(p)
}

/**
 * Отрезок между двумя точками
 */
data class Segment(val begin: Point, val end: Point) {
    override fun equals(other: Any?) =
            other is Segment && (begin == other.begin && end == other.end || end == other.begin && begin == other.end)

    override fun hashCode() =
            begin.hashCode() + end.hashCode()
}

/**
 * Средняя
 *
 * Дано множество точек. Вернуть отрезок, соединяющий две наиболее удалённые из них.
 * Если в множестве менее двух точек, бросить IllegalArgumentException
 */
fun diameter(vararg points: Point): Segment {
    val pointsList = points.toList()
    if (pointsList.size < 2) throw IllegalArgumentException()
    var output = Segment(pointsList[0], pointsList[1])
    var distance = 0.0

    for (i in 0..pointsList.size - 2) {
        for (j in i + 1 until pointsList.size) {
            val currentDistance = pointsList[i].distance(pointsList[j])
            if (distance < currentDistance) {
                output = Segment(pointsList[i], pointsList[j])
                distance = currentDistance
            }
        }
    }
    return output
}

/**
 * Простая
 *
 * Построить окружность по её диаметру, заданному двумя точками
 * Центр её должен находиться посередине между точками, а радиус составлять половину расстояния между ними
 */
fun circleByDiameter(diameter: Segment): Circle {
    val newCenter = Point((diameter.begin.x + diameter.end.x) / 2, (diameter.end.y + diameter.begin.y) / 2)
    val newRad = diameter.begin.distance(diameter.end) / 2
    return Circle(newCenter, newRad)
}

/**
 * Прямая, заданная точкой point и углом наклона angle (в радианах) по отношению к оси X.
 * Уравнение прямой: (y - point.y) * cos(angle) = (x - point.x) * sin(angle)
 * или: y * cos(angle) = x * sin(angle) + b, где b = point.y * cos(angle) - point.x * sin(angle).
 * Угол наклона обязан находиться в диапазоне от 0 (включительно) до PI (исключительно).
 */
class Line private constructor(val b: Double, val angle: Double) {
    init {
        assert(angle >= 0 && angle < Math.PI) { "Incorrect line angle: $angle" }
    }

    constructor(point: Point, angle: Double) : this(point.y * Math.cos(angle) - point.x * Math.sin(angle), angle)

    /**
     * Средняя
     *
     * Найти точку пересечения с другой линией.
     * Для этого необходимо составить и решить систему из двух уравнений (каждое для своей прямой)
     */
    fun crossPoint(other: Line): Point = TODO()

    override fun equals(other: Any?) = other is Line && angle == other.angle && b == other.b

    override fun hashCode(): Int {
        var result = b.hashCode()
        result = 31 * result + angle.hashCode()
        return result
    }

    override fun toString() = "Line(${Math.cos(angle)} * y = ${Math.sin(angle)} * x + $b)"
}

/**
 * Средняя
 *
 * Построить прямую по отрезку
 */
fun lineBySegment(s: Segment): Line {
    val angle = if (s.begin.x == s.end.x) PI / 2
    else atan((s.end.y - s.begin.y) / (s.end.x - s.begin.x))

    return Line(s.begin, angle)
}

/**
 * Средняя
 *
 * Построить прямую по двум точкам
 */
fun lineByPoints(a: Point, b: Point): Line = lineBySegment(Segment(a, b))

/**
 * Сложная
 *
 * Построить серединный перпендикуляр по отрезку или по двум точкам
 */

fun bisectorByPoints(a: Point, b: Point): Line {
    val newAngle = lineByPoints(a, b).angle
    val newCenter = Point((a.x + b.x) / 2, (a.y + b.y) / 2)

    return if (newAngle >= PI / 2) Line(newCenter, newAngle - PI / 2)
    else Line(newCenter, newAngle + PI / 2)
}

/**
 * Средняя
 *
 * Задан список из n окружностей на плоскости. Найти пару наименее удалённых из них.
 * Если в списке менее двух окружностей, бросить IllegalArgumentException
 */
fun findNearestCirclePair(vararg circles: Circle): Pair<Circle, Circle> {
    val circlesList = circles.toList()
    if (circlesList.size < 2) throw IllegalArgumentException()

    var distance = circlesList[0].distance(circlesList[1])
    var output = Pair(circlesList[0], circlesList[1])

    for (i in 0..circlesList.size - 2) {
        for (j in i + 1 until circlesList.size) {
            val currentDistance = circlesList[i].distance(circlesList[j])
            if (distance > currentDistance) {
                distance = currentDistance
                output = Pair(circlesList[i], circlesList[j])
            }
        }
    }
    return output
}

/**
 * Сложная
 *
 * Дано три различные точки. Построить окружность, проходящую через них
 * (все три точки должны лежать НА, а не ВНУТРИ, окружности).
 * Описание алгоритмов см. в Интернете
 * (построить окружность по трём точкам, или
 * построить окружность, описанную вокруг треугольника - эквивалентная задача).
 */

fun circleByThreePoints(a: Point, b: Point, c: Point): Circle {
    if ((c.x - a.x) / (b.x - a.x) == (c.y - a.y) / (b.y - a.y))   //проверка лежат ли на одной прямой
        return circleByDiameter(diameter(a, b, c))

    val pointA = b.x - a.x
    val pointB = b.y - a.y
    val pointC = c.x - a.x
    val pointD = c.y - a.y
    val pointE = pointA * (a.x + b.x) + pointB * (a.y + b.y)
    val pointF = pointC * (a.x + c.x) + pointD * (a.y + c.y)
    val pointG = 2 * (pointA * (c.y - b.y) - pointB * (c.x - b.x))

    val centerX = (pointD * pointE - pointB * pointF) / pointG
    val centerY = (pointA * pointF - pointC * pointE) / pointG
    val centerPoint = Point(centerX, centerY)

    return Circle(centerPoint, centerPoint.distance(a))
}

/**
 * Очень сложная
 *
 * Дано множество точек на плоскости. Найти круг минимального радиуса,
 * содержащий все эти точки. Если множество пустое, бросить IllegalArgumentException.
 * Если множество содержит одну точку, вернуть круг нулевого радиуса с центром в данной точке.
 *
 * Примечание: в зависимости от ситуации, такая окружность может либо проходить через какие-либо
 * три точки данного множества, либо иметь своим диаметром отрезок,
 * соединяющий две самые удалённые точки в данном множестве.
 */
fun minContainingCircle(vararg points: Point): Circle {
    if (points.size == 0) throw IllegalArgumentException()
    if (points.size == 1) return Circle(points[0], 0.0)
    if (points.size == 2) return circleByDiameter(diameter(*points))
    return circleByThreePoints(diameter(*points).begin, diameter(*points).end, getPoint(*points))
}

fun getPoint(vararg points: Point): Point {
    val newCircle = circleByDiameter(diameter(*points))
    var outputPoint = newCircle.center
    var count = 0

    for (point in points) {
        if (newCircle.center.distance(point) > newCircle.center.distance(outputPoint) && !newCircle.contains(point)) {
            outputPoint = point
            count++
        }
    }
    return if (count == 0) {
        diameter(*points).begin
    } else outputPoint
}

