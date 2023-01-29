package com.child.parent.kidcare.customview.timepicker;



public  class Assist {

        public  double to_0_360(double angle) {
            double result = angle % (double)360;
            if (result < (double)0) {
                result += (double)360;
            }

            return result;
        }

        public  double to_0_720(double angle) {
            double result = angle % (double)720;
            if (result < (double)0) {
                result += (double)720;
            }

            return result;
        }

        public  double minutesToAngle(int mins) {
            return this.to_0_720((double)90 - (double)mins / 720.0D * 360.0D);
        }

        public  int angleToMins(double angle) {
            return (int)((this).to_0_720((double)90 - angle) / (double)360 * (double)12 * (double)60);
        }

        public  double angleBetweenVectors(double angle1, double angle2) {
            double x1 = Math.cos(angle1);
            double y1 = Math.sin(angle1);
            double x2 = Math.cos(angle2);
            double y2 = Math.sin(angle2);
            return this.vectorsAngleRad(x1, y1, x2, y2);
        }

        private  double cross(double x1, double y1, double x2, double y2) {
            return x1 * y2 - y1 * x2;
        }

        private  double dot(double x1, double y1, double x2, double y2) {
            return x1 * x2 + y1 * y2;
        }

        public  double vectorsAngleRad(double x1, double y1, double x2, double y2) {
            double var9 = this.cross(x1, y1, x2, y2);
            double var11 = this.dot(x1, y1, x2, y2);
            return Math.atan2(var9, var11);
        }

        public  int snapMinutes(int minutes, int step) {
            return minutes / step * step + 2 * (minutes % step) / step * step;
        }


}

