package com.mastercom.rcc.util;

import java.util.List;

import com.mastercom.rcc.model.Point;

public class PolygonUtil {

	
	public static boolean isPointInOrOnPolygon(double x, double y, List<Point> Vertexes)
    {
        int intResult = checkPointInOrOnPolygon(x, y, Vertexes);
        return intResult != 0;
    }

	public static int checkPointInOrOnPolygon(double x, double y, List<Point> vertexes)
    {
        if (vertexes == null || vertexes.size() == 0)
        {
            return 0;
        }
        //See "The Point in Polygon Problem for Arbitrary lstPolygons" by Hormann & Agathos
        //http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.88.5498&rep=rep1&type=pdf
        int intResult = 0;//0在多边形外，1在多边形里面，-1在多边形边界上
        int cnt = vertexes.size();
        Point pt1 = vertexes.get(0);
        for (int i = 1; i <= cnt; ++i)
        {
            Point ptNext = (i == cnt ? vertexes.get(0) : vertexes.get(i));
            if (ptNext.y == y)
            {
                if ((ptNext.x == x) || (pt1.y == y && ((ptNext.x > x) == (pt1.x < x))))
                {
                    return -1;
                }
            }

            if ((pt1.y < y) != (ptNext.y < y))
            {
                if (pt1.x >= x)
                {
                    if (ptNext.x > x)
                    {
                        intResult = 1 - intResult;
                    }
                    else
                    {
                        double d = (pt1.x - x) * (ptNext.y - y)
                            - (ptNext.x - x) * (pt1.y - y);
                        if (d == 0)
                        {
                            return -1;
                        }
                        else if ((d > 0) == (ptNext.y > pt1.y))
                        {
                            intResult = 1 - intResult;
                        }
                    }
                }
                else
                {
                    if (ptNext.x > x)
                    {
                        double d = (pt1.x - x) * (ptNext.y - y)
                            - (ptNext.x - x) * (pt1.y - y);
                        if (d == 0)
                        {
                            return -1;
                        }
                        else if ((d > 0) == (ptNext.y > pt1.y))
                        {
                            intResult = 1 - intResult;
                        }
                    }
                }
            }
            pt1 = ptNext;
        }
        return intResult;
    }

}
