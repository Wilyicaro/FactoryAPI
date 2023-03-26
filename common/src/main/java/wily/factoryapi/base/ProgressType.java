package wily.factoryapi.base;


import net.minecraft.resources.ResourceLocation;

public class ProgressType {


    public final String name;

    public final Identifier identifier;
    public final int uvX;

    public final int uvY;
    public final int sizeX;
    public final int sizeY;

    public final boolean isReverse;
    public final Direction Plane;


    public final boolean hasFluid;

    public final ResourceLocation texture;
    public ProgressType( Identifier identifier,ResourceLocation texture, int[] uvSize, boolean hasFluid, boolean reverse, Direction plane) {
        this.name = identifier.name;
        this.texture = texture;
        this.identifier = identifier;
        this.uvX = uvSize[0];
        this.uvY = uvSize[1];
        this.sizeX = uvSize[2];
        this.sizeY = uvSize[3];
        this.hasFluid = hasFluid;
        this.isReverse = reverse;
        this.Plane = plane;


    }

    public boolean inMouseLimit(int mouseX, int mouseY, int posX, int posY){
        return getMouseLimit(mouseX,mouseY,posX,posY,sizeX,sizeY);
    }

    public static boolean getMouseLimit(double mouseX, double mouseY, int posX, int posY, int sizeX, int sizeY){
        return (mouseX >= posX && mouseX <= posX + sizeX && mouseY >= posY && mouseY <= posY + sizeY);
    }
public enum Direction {
    VERTICAL,HORIZONTAL
}

    public enum Identifier {
        DEFAULT("progress"),ENERGY_STORAGE("energyStorage"),TANK("tank"), BURN_TIME("burnTime"),GENERATING("gen");

        public final String name;
        Identifier(String name){this.name = name;}
    }
}