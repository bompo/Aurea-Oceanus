package de.swagner.ld22;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Application.ApplicationType;

public class Configuration {
	
	public Preferences preferences;
	public boolean fullscreen;
	public float brighness = 0.0f;
	
	static Configuration instance;
	
	private Configuration() {
		preferences = Gdx.app.getPreferences("AureaOceanus");
		loadConfig();
	}
	
	private void loadConfig() {
		fullscreen = preferences.getBoolean("fullscreen", false);
		if (Gdx.app.getType() == ApplicationType.Android) {
			brighness = preferences.getFloat("brighness", 0.2f);
		} else {
			brighness = preferences.getFloat("brighness", 0.05f);
		}
	}
	
	public void setConfiguration() {
		if(Gdx.app.getType() == ApplicationType.Desktop) {
			if(fullscreen) {
				Gdx.graphics.setDisplayMode(Gdx.graphics.getDesktopDisplayMode().width, Gdx.graphics.getDesktopDisplayMode().height, true);
			} else {
				Gdx.graphics.setDisplayMode(800,480, false);		
			}
		}
		setBrighness(brighness);
	}
	
	public void setFullscreen(boolean onOff) {
		preferences.putBoolean("fullscreen", onOff);
		fullscreen = onOff;
		preferences.flush();
	}
	
	public void setBrighness(float brighness) {
		preferences.putFloat("brighness", brighness);
		this.brighness = brighness;
		preferences.flush();
		
		Resources.getInstance().clearColor[0] = Resources.getInstance().clearColorRef[0] + brighness;
		Resources.getInstance().clearColor[1] = Resources.getInstance().clearColorRef[1] + brighness;  
		Resources.getInstance().clearColor[2] = Resources.getInstance().clearColorRef[2] + brighness;  
		Resources.getInstance().clearColor[3] = Resources.getInstance().clearColorRef[3];
		
		Resources.getInstance().fogColor[0] = Resources.getInstance().fogColorRef[0] + brighness;
		Resources.getInstance().fogColor[1] = Resources.getInstance().fogColorRef[1] + brighness;  
		Resources.getInstance().fogColor[2] = Resources.getInstance().fogColorRef[2] + brighness;  
		Resources.getInstance().fogColor[3] = Resources.getInstance().fogColorRef[3];
		
		
		Gdx.gl.glClearColor(Resources.getInstance().clearColor[0], Resources.getInstance().clearColor[1], Resources.getInstance().clearColor[2],
				Resources.getInstance().clearColor[3]);
	}
	
	public static Configuration getInstance() {
		if(instance!=null) return instance;
		instance = new Configuration();		
		return instance;
	}	
	


}
