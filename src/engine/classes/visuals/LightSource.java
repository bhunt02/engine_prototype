package classes.visuals;

import classes.data_types.RGB;
import classes.utilities.VMath;
import classes.data_types.Vector2;
public class LightSource {
    public Vector2 Position = new Vector2();
    double intensity = 1.0;
    public double volumetricIntensity = 1.0;
    double minAngle = 0;
    double maxAngle = 2*Math.PI;
    RGB lightTint = new RGB(255,255,255);
    public double getRadialFalloff(Vector2 otherPosition) {
        double distance = VMath.magnitude(this.Position,otherPosition);
        return Math.pow(1.0 - distance, 2.0);
    }
    public double getAngularFalloff(Vector2 otherPosition) {
        double theta = Math.tan(otherPosition.y/Position.x);
        return VMath.smoothstep(minAngle, maxAngle, theta);
    }
    public double getNormalFalloff(Vector2 otherPosition) {
        return VMath.clamp(VMath.dot(Position,otherPosition),0.0,1.0);
    }
}
