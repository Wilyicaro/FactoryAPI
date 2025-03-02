package wily.factoryapi.util;

public class ColorUtil {
    public static int getR(int color) {
        return (color >> 16 & 0xFF);
    }

    public static int getG(int color) {
        return (color >> 8 & 0xFF);
    }

    public static int getB(int color) {
        return (color & 0xFF);
    }

    public static int getA(int color) {
        return (color >> 24 & 0xFF);
    }

    public static float getRed(int color) {
        return getR(color) / 255.0F;
    }

    public static float getGreen(int color) {
        return getG(color) / 255.0F;
    }

    public static float getBlue(int color) {
        return getB(color) / 255.0F;
    }

    public static float getAlpha(int color) {
        return getA(color) / 255.0F;
    }

    public static int toInt(float channel) {
        return (int)(channel * 255);
    }

    public static int colorFromInt(int r, int g, int b, int a) {
        return a << 24 | r << 16 | g << 8 | b;
    }

    public static int colorFromFloat(float r, float g, float b, float a) {
        return colorFromInt(toInt(r), toInt(g), toInt(b), toInt(a));
    }

    public static int colorFromFloat(float[] rgba) {
        return colorFromFloat(rgba.length == 0 ? 0 : rgba[0], rgba.length <= 1 ? 0 : rgba[1], rgba.length <= 2 ? 0 : rgba[2], rgba.length <= 3 ? 0 : rgba[3]);
    }

    public static float[] rgbaToFloat(int rgba) {
        return new float[]{getRed(rgba),getGreen(rgba),getBlue(rgba),getAlpha(rgba)};
    }

    public static int withAlpha(int color, int alpha){
        return colorFromInt(getR(color), getG(color), getB(color), alpha);
    }

    public static int withAlpha(int color, float alpha){
        return colorFromInt(getR(color), getG(color), getB(color), toInt(alpha));
    }

    public static int mergeColors(int color, int color1){
        if (color == -1) return color1;
        else if (color1 == -1) return color;
        return colorFromFloat(getRed(color)*getRed(color1), getGreen(color)*getGreen(color1), getBlue(color)*getBlue(color1), getAlpha(color)*getAlpha(color1));
    }
}
