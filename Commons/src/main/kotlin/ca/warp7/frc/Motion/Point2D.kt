package ca.warp7.frc.Motion

import koma.extensions.map
import koma.mat
import koma.matrix.Matrix
import koma.pow
import koma.sqrt


val Matrix<Double>.hypotenuse get() = sqrt( map{ it.pow(2.0)}.elementSum())
fun things(): Unit{
    val points = arrayOf(
            mat[0,1],
            mat[1,2],
            mat[2,3]
    )

    val distances = mutableListOf<Double>()
    for (i in 1 until points.size){
        distances.add((points[i] - points[i - 1]).hypotenuse)
    }

}