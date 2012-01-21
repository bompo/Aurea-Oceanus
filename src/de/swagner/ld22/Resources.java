package de.swagner.ld22;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import de.swagner.ld22.shader.DiffuseShader;
import de.swagner.ld22.shader.DiffuseShaderFog;
import de.swagner.ld22.shader.DiffuseShaderFogWobble;
import de.swagner.ld22.shader.DiffuseShaderFogWobbleShark;

public class Resources {
	
	public static final boolean POSTPROCESSING = true;

	public final boolean debugMode = true;

	public ShaderProgram diffuseShader;
	public ShaderProgram diffuseShaderFog;
	public ShaderProgram diffuseShaderFogWobble;
	public ShaderProgram diffuseShaderFogWobbleShark;
	
	public Music breath;
	public Music fastBreath;
	public Music intro;
	public Sound pickUp;
	public Sound danger;
	public Sound noBreath;
	public Sound getBite;
	public Sound footStep;

	public BitmapFont font;

	public float[] clearColorRef = { 0.01f, 0.03f, 0.1f, 1.0f };
	public float[] fogColorRef = { 0.01f, 0.03f, 0.1f, 1.0f };
	
	public float[] clearColor = { 0.01f, 0.03f, 0.1f, 1.0f };
	public float[] fogColor ={ 0.01f, 0.03f, 0.1f, 1.0f };

	public static Resources instance;

	public static Resources getInstance() {
		if (instance == null) {
			instance = new Resources();
		}
		return instance;
	}

	public Resources() {
		reInit();
	}

	public void reInit() {				
		initShader();
		
		breath = Gdx.audio.newMusic(Gdx.files.internal("data/breath.ogg"));
		fastBreath = Gdx.audio.newMusic(Gdx.files.internal("data/fastBreath.ogg"));
		intro = Gdx.audio.newMusic(Gdx.files.internal("data/intro2.ogg"));
		pickUp = Gdx.audio.newSound(Gdx.files.internal("data/pickUp.ogg"));
		danger = Gdx.audio.newSound(Gdx.files.internal("data/danger.ogg"));
		noBreath = Gdx.audio.newSound(Gdx.files.internal("data/noBreath.ogg"));
		getBite = Gdx.audio.newSound(Gdx.files.internal("data/getBite.ogg"));
		footStep = Gdx.audio.newSound(Gdx.files.internal("data/footSteps.ogg"));

		font = new BitmapFont();
	}

	public void initShader() {
		diffuseShader = new ShaderProgram(DiffuseShader.mVertexShader, DiffuseShader.mFragmentShader);
		if (diffuseShader.isCompiled() == false) {
			Gdx.app.log("diffuseShader: ", diffuseShader.getLog());
//			System.exit(0);
		}
		
		diffuseShaderFog = new ShaderProgram(DiffuseShaderFog.mVertexShader, DiffuseShaderFog.mFragmentShader);
		if (diffuseShaderFog.isCompiled() == false) {
			Gdx.app.log("diffuseShaderFog: ", diffuseShaderFog.getLog());
//			System.exit(0);
		}
		
		diffuseShaderFogWobble = new ShaderProgram(DiffuseShaderFogWobble.mVertexShader, DiffuseShaderFogWobble.mFragmentShader);
		if (diffuseShaderFogWobble.isCompiled() == false) {
			Gdx.app.log("diffuseShaderFogWobble: ", diffuseShaderFogWobble.getLog());
//			System.exit(0);
		}
		
		
		diffuseShaderFogWobbleShark = new ShaderProgram(DiffuseShaderFogWobbleShark.mVertexShader, DiffuseShaderFogWobbleShark.mFragmentShader);
		if (diffuseShaderFogWobbleShark.isCompiled() == false) {
			Gdx.app.log("diffuseShaderFogWobbleShark: ", diffuseShaderFogWobbleShark.getLog());
//			System.exit(0);
		}
	}

	public void dispose() {
		font.dispose();
		
		diffuseShader.dispose();
		diffuseShaderFog.dispose();
	}
}
