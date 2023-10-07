package wily.factoryapi.base;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import java.awt.*;


public class SlotsIdentifier {
    public static final SlotsIdentifier WATER = new SlotsIdentifier(ChatFormatting.BLUE,"water");
    public static final SlotsIdentifier LAVA= new SlotsIdentifier(ChatFormatting.GOLD,"lava");
    public static final  SlotsIdentifier INPUT = new SlotsIdentifier(ChatFormatting.BLUE,"input");
    public static final  SlotsIdentifier OUTPUT = new SlotsIdentifier(ChatFormatting.RED,"output");
    public static final  SlotsIdentifier FUEL = new SlotsIdentifier(ChatFormatting.GOLD,"fuel");
    public static final  SlotsIdentifier PURPLE =  new SlotsIdentifier(ChatFormatting.DARK_PURPLE,"input_output");
    public static final  SlotsIdentifier ENERGY =  new SlotsIdentifier(ChatFormatting.AQUA,"energy");
    public static final  SlotsIdentifier GENERIC = new SlotsIdentifier(ChatFormatting.GRAY,"single");

    public final ChatFormatting color;
    public final String name;
    public final Component component;
    public SlotsIdentifier(ChatFormatting color, String name,Component component){
        this.color = color;
        this.name = name;
        this.component = component;
    }
    public SlotsIdentifier(ChatFormatting color, String name){
        this(color,name,new TranslatableComponent("tooltip.factory_api.config.identifier." + name));
    }

    public Color getColor(){
        return new Color(color.getColor());
    }

    public String getFormattedName(){
        return component.getString();
    }
    public Component getTooltip(String type){
        return new TranslatableComponent("tooltip.factory_api.config." + type, getFormattedName());
    }
    public String getName() {
        return this.name;

    }
}
