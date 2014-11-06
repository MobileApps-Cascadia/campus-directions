package com.deitel.cannongame;


public class Path
{
   // Enhanced variable section
   public float x, y;	// cannon ball path dot/line
   public float cannonPathRadius;	// radius of each path dot, increase size over distance	
   
   public Path(float xx, float yy, float r)
   {
	   x = (float) xx; y = (float) yy; cannonPathRadius = (float) r;
   }
   
   public Path()
   {
	   x = 10; y=10; cannonPathRadius = (float) .01;
   }
}