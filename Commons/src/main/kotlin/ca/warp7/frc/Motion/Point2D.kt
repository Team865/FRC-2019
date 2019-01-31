package ca.warp7.frc.Motion

object Point2D {
    var  kX = 0.0
    var kY = 0.0
    var kHypotenuse = 0.0

    fun setXY(x: Double, y: Double){
        kX = x
        kY = y
        kHypotenuse = Math.sqrt(Math.pow(x, 2.0) + Math.pow(y, 2.0))
    }

    fun getX(): Double { return kX    }
    fun getY(): Double { return kY    }
    fun getHypotenuse(): Double { return kHypotenuse   }
}