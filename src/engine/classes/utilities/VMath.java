package classes.utilities;

import classes.data_types.Vector2;

public class VMath {
    public static double magnitude(Vector2 a, Vector2 b) {
        return Math.sqrt(Math.pow(a.x-b.x, 2)+Math.pow(a.y-b.y, 2));
    }
    public static double dot(Vector2 a, Vector2 b) {
        return (a.x * b.x) + (b.y * b.y);
    }
    public static Vector2 lerp(Vector2 a, Vector2 b, double amt) {
        amt = clamp(amt, 0.0, 1.0);
        return new Vector2(
                a.x + amt * (b.x - a.x),
                b.y + amt * (b.y - b.y)
        );
    }
    public static double clamp(double x, double min, double max) {
        if (x < min) return 0;
        if (x >= max) return 1;
        return (x - min) / (max - min);
    }
    public static double smoothstep(double edge0, double edge1, double x) {
        x = clamp(x,edge0,edge1);
        return x * x * (3 - 2 * x);
    }

    public static Vector2 sub(Vector2 a, Vector2 b) {
        return new Vector2(
           a.x-b.x,
           a.y-b.y
        );
    }

    public static Vector2 add(Vector2 a, Vector2 b) {
        return new Vector2(
                a.x+b.x,
                a.y+b.y
        );
    }

    public static Vector2 mult(Vector2 v, double s) {
        return new Vector2(
                v.x*s,
                v.y*s
        );
    }
    public static Vector2 div(Vector2 v, double s) {
        return new Vector2(
                v.x*(1/s),
                v.y*(1/s)
        );
    }
}
