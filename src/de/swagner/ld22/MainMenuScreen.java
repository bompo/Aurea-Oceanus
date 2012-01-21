package de.swagner.ld22;

import java.io.IOException;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.loaders.ModelLoaderRegistry;
import com.badlogic.gdx.graphics.g3d.model.still.StillModel;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class MainMenuScreen extends DefaultScreen implements InputProcessor {

	float startTime = 0;
	PerspectiveCamera cam;
	
	StillModel modelPlaneObj;
	StillModel modelPlaneHighObj;
	StillModel modelSuitObj;
	StillModel modelShipObj;
	StillModel modelStoneObj;
	StillModel modelLeafObj;
	StillModel modelItemObj;
	StillModel modelSharkObj;
	
	Texture modelWhiteTex;
	Texture modelSuitTex;
	Texture modelShipTex;
	Texture modelLensTex;
	Texture modelStoneTex;
	Texture modelLeafTex;
	Texture modelOverSeaTex;
	Texture modelDownSeaTex;
	Texture modelItemTex;
	Texture modelSharkTex;
	
	ImmediateModeRenderer20 renderer;

	Music music;
	Sound pickUp;
	Sound danger;

	SpriteBatch batch;
	SpriteBatch fadeBatch;
	SpriteBatch fontbatch;
	BitmapFont font;
	Sprite blackFade;
	
	Array<Stone> stones = new Array<Stone>();
	Array<Stone> bigStones = new Array<Stone>();
	Array<Leaf> leafs = new Array<Leaf>();
	Array<Item> items = new Array<Item>();
	Array<Shark> sharks = new Array<Shark>();
	Submarine submarine;
	
	
	Vector3 sharkTarget = new Vector3(MathUtils.random(-100,100) ,0 , MathUtils.random(-100,100));


	Player player = new Player();

	float fade = 1.0f;
	boolean finished = false;

	float delta;

	// GLES20
	Matrix4 model = new Matrix4().idt();
	Matrix4 normal = new Matrix4().idt();
	Matrix4 tmp = new Matrix4().idt();

	private ShaderProgram diffuseShader;
	private ShaderProgram diffuseShaderFog;
	private ShaderProgram diffuseShaderFogWobble;
	private ShaderProgram diffuseShaderFogWobbleShark;

	public MainMenuScreen(Game game) {
		super(game);
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setInputProcessor(this);
		
		modelSuitObj = ModelLoaderRegistry.loadStillModel(Gdx.files.internal("data/suite.g3dt"));
		modelSuitTex = new Texture(Gdx.files.internal("data/suite_light.png"));
		
		modelShipObj = ModelLoaderRegistry.loadStillModel(Gdx.files.internal("data/ship.g3dt"));
		modelShipTex = new Texture(Gdx.files.internal("data/ship_light.png"));
		modelShipTex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
						
		modelPlaneObj = ModelLoaderRegistry.loadStillModel(Gdx.files.internal("data/plane.g3dt"));	
		modelWhiteTex = new Texture(Gdx.files.internal("data/black.png"));
		modelLensTex = new Texture(Gdx.files.internal("data/lens.png"));
		modelLensTex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		modelOverSeaTex = new Texture(Gdx.files.internal("data/overthedeepsea.png"));
		modelDownSeaTex = new Texture(Gdx.files.internal("data/downthedeepsea.png"));
		modelDownSeaTex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		modelStoneObj = ModelLoaderRegistry.loadStillModel(Gdx.files.internal("data/stone.g3dt"));
		modelStoneTex = new Texture(Gdx.files.internal("data/stone_light.png"));
		
		modelLeafObj = ModelLoaderRegistry.loadStillModel(Gdx.files.internal("data/leaf.g3dt"));
		modelLeafTex = new Texture(Gdx.files.internal("data/stone_light.png"));
		
		modelItemObj = ModelLoaderRegistry.loadStillModel(Gdx.files.internal("data/item.g3dt"));
		modelItemTex = new Texture(Gdx.files.internal("data/item_light.png"));
		
		modelSharkObj = ModelLoaderRegistry.loadStillModel(Gdx.files.internal("data/shark.g3dt"));
		modelSharkTex = new Texture(Gdx.files.internal("data/shark_light.png"));
		
		
		batch = new SpriteBatch();
		batch.getProjectionMatrix().setToOrtho2D(0, 0, 800, 480);
		fontbatch = new SpriteBatch();

		blackFade = new Sprite(new Texture(Gdx.files.internal("data/black.png")));
		fadeBatch = new SpriteBatch();
		fadeBatch.getProjectionMatrix().setToOrtho2D(0, 0, 1, 1);

		font = Resources.getInstance().font;
		font.setScale(1);

		diffuseShaderFog = Resources.getInstance().diffuseShaderFog;
		diffuseShaderFogWobble = Resources.getInstance().diffuseShaderFogWobble;
		diffuseShaderFogWobbleShark = Resources.getInstance().diffuseShaderFogWobbleShark;
		diffuseShader = Resources.getInstance().diffuseShader;	

		pickUp = Resources.getInstance().pickUp;
		danger = Resources.getInstance().danger;
		
		music = Resources.getInstance().intro;
		music.stop();
		music.setVolume(1f);
		music.setLooping(true);
		music.play();	
		
		
		initRender();
		initLevel();
	}

	public void initRender() {
		Gdx.graphics.getGL20().glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		Gdx.gl.glClearColor(Resources.getInstance().clearColor[0], Resources.getInstance().clearColor[1], Resources.getInstance().clearColor[2],
				Resources.getInstance().clearColor[3]);
				
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		cam = new PerspectiveCamera(45, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(0, 25.5f, 16f);
		cam.direction.set(0, -1.5f, -1);
		cam.up.set(0, 1, 0);
		cam.near = 1f;
		cam.far = 50f;
		initRender();
	}

	private void initLevel() {
		Vector3 position = new Vector3();
		position = new Vector3(0, 8, 0);
		submarine = new Submarine(position, 6,0, 120);
		
		for (int i = 0; i < 2000; i++) {
			position = new Vector3();
			boolean add = false;
			position = new Vector3(MathUtils.random(-100, 100), -1, MathUtils.random(-100, 100));
			stones.add(new Stone(position, MathUtils.random(100, 180)/100.f , MathUtils.random(0,
					2), MathUtils.random(0, 360)));
		}
		
		for (int i = 0; i < 15; i++) {
			position = new Vector3();
			boolean add = false;
//			while(!add) {
				position = new Vector3(MathUtils.random(-70, 70), 6, MathUtils.random(-70, 70));
//				add = true;
//				if((position.x < 80 && position.x > -80) 
//						|| (position.z < 80  && position.z > -80)) {
//					add = false;
////					System.out.println("no sharks allowd here: " + position);
//				}
//			}
//			System.out.println("set shark to: " + position);
			sharks.add(new Shark(position));
		}
		
		for (int i = 0; i < 1500; i++) {
			position = new Vector3();
			boolean add = false;
			if(MathUtils.randomBoolean()) {
				if(MathUtils.randomBoolean()) {
					position = new Vector3(-160, -1, MathUtils.random(-100, 100));
				} else {
					position = new Vector3(160, -1, MathUtils.random(-100, 100));
				}
			} else {
				if(MathUtils.randomBoolean()) {
					position = new Vector3(MathUtils.random(-100, 100), -1,-160);
				} else {
					position = new Vector3(MathUtils.random(-100, 100), -1,160);
				}
			}
			bigStones.add(new Stone(position, MathUtils.random(1000, 1800)/100.f , MathUtils.random(0,
					2), MathUtils.random(0, 360)));
		}
		
//		bigStones.add(new Stone(new Vector3(10,-1,10), MathUtils.random(1000, 1800)/100.f , MathUtils.random(0,
//				2), MathUtils.random(0, 360)));
		
		for (int i = 0; i < 1000; i++) {
			position = new Vector3();
			boolean add = false;
			position = new Vector3(MathUtils.random(-100, 100), -1, MathUtils.random(-100, 100));
			leafs.add(new Leaf(position, MathUtils.random(30, 60)/100.f , MathUtils.random(0,
					8), MathUtils.random(0, 360)));
		}
		
		for (int i = 0; i < 10; i++) {
			position = new Vector3();
			boolean add = false;
			while(!add) {
				position = new Vector3(MathUtils.random(-100, 100), -1, MathUtils.random(-100, 100));
				add = true;
				for (int n = 0; n< stones.size; n++) {
					if((stones.get(n).position.x <= position.x+4 && stones.get(n).position.x >= position.x-4) 
							&& (stones.get(n).position.z <= position.z+4 && stones.get(n).position.z >= position.z-4)) {
						add = false;
//						System.out.println("blocked");
						break;
					}
				}
			}
			items.add(new Item(position, MathUtils.random(100, 100)/100.f , MathUtils.random(0, 360), MathUtils.random(0, 360)));
		}
		
		calculateModelMatrix();
	}
	
	private void calculateModelMatrix() {
		submarine.calculateMatrix();
		
		for (int i = 0; i < stones.size; ++i) {
			stones.get(i).calculateMatrix();
		}
		for (int i = 0; i < bigStones.size; ++i) {
			bigStones.get(i).calculateMatrix();
		}
		for (int i = 0; i < leafs.size; ++i) {
			leafs.get(i).calculateMatrix();
		}
		for (int i = 0; i < items.size; ++i) {
			items.get(i).calculateMatrix();
		}
		for (int i = 0; i < sharks.size; ++i) {
			sharks.get(i).calculateMatrix();
		}
	}


	private void reset() {
		initLevel();
	}

	@Override
	public void show() {
	}

	@Override
	public void render(float deltaTime) {
		Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
		delta = Math.min(0.1f, deltaTime);

		startTime += delta;
		
		if(startTime%20 == 0) {
			sharkTarget = new Vector3(MathUtils.random(-100,100) ,0 , MathUtils.random(-100,100)); 
		}
		
		cam.position.z += MathUtils.sin(startTime)/100.f;
		cam.position.x += MathUtils.sin(startTime)/100.f;
		
		cam.update();
		
		//update sharks FOR INTRO!!
		for (int i = 0; i < sharks.size; ++i) {
			sharks.get(i).update(sharkTarget,delta);
		}
		
		renderScene(); 
		
		batch.begin();
			font.setScale(5);
			font.draw(batch, "Aurea Oceanus",20, 240);
			font.setScale(1);
			font.drawMultiLine(batch, "Collect enough gold and return to the submarine!\nBeware of the sharks! Run to avoid them!\nBut take into account that you will need more oxygen if you run.\n\nmade by bompo (@twbompo)\nat ludum dare #22",20, 120);		
			font.drawMultiLine(batch, "Mouse: look\nWASD: move\nHold Shift: run\nF: Fullscreen\nF8: Screenshot\n+/-: Brightness\n"+Gdx.graphics.getFramesPerSecond(),650, 120);		
		batch.end();
		

		// FadeInOut
		if (!finished && fade > 0) {
			fade = Math.max(fade - (delta), 0);
			fadeBatch.begin();
			blackFade.setColor(blackFade.getColor().r, blackFade.getColor().g, blackFade.getColor().b, fade);
			blackFade.draw(fadeBatch);
			fadeBatch.end();
		}

		if (finished) {
			fade = Math.min(fade + (delta), 1);
			fadeBatch.begin();
			blackFade.setColor(blackFade.getColor().r, blackFade.getColor().g, blackFade.getColor().b, fade);
			blackFade.draw(fadeBatch);
			fadeBatch.end();
			if (fade >= 1) {
				music.setVolume(0.5f);
				music.stop();
				game.setScreen(new GameScreen(game));
			}
		}
	}

	private void renderScene() {	
		diffuseShaderFog.begin();
		diffuseShaderFog.setUniformMatrix("VPMatrix", cam.combined);
		diffuseShaderFog.setUniformf("uFogColor", Resources.getInstance().fogColor[0],Resources.getInstance().fogColor[1],Resources.getInstance().fogColor[2],Resources.getInstance().fogColor[3]);
		diffuseShaderFog.setUniformi("uSampler", 0);
				
		// render ground
		tmp.idt();
		model.idt();

		tmp.setToTranslation(18,-1.1f, 0);
		model.mul(tmp);
		
		tmp.setToScaling(1000,1000,1000);
		model.mul(tmp);
			
		diffuseShaderFog.setUniformMatrix("MMatrix", model);
		
		modelDownSeaTex.bind(0);
		modelPlaneObj.render(diffuseShaderFog);
			
		
        
		// render all rocks
		modelWhiteTex.bind(0);
		for (int i = 0; i < stones.size; ++i) {
			if(!cam.frustum.sphereInFrustum(stones.get(i).collisionPosition,2)) continue;

			diffuseShaderFog.setUniformMatrix("MMatrix", stones.get(i).model);
			
			modelStoneObj.render(diffuseShaderFog);
		}
		
		// render all big rocks
		for (int i = 0; i < bigStones.size; ++i) {
			if(!cam.frustum.sphereInFrustum(bigStones.get(i).collisionPosition,40)) continue;

			diffuseShaderFog.setUniformMatrix("MMatrix", bigStones.get(i).model);
			
			modelStoneObj.render(diffuseShaderFog);
		}
		
		// render all items
		modelItemTex.bind(0);
		for (int i = 0; i < items.size; ++i) {
			if(!cam.frustum.sphereInFrustum(items.get(i).collisionPosition,1)) continue;

			diffuseShaderFog.setUniformMatrix("MMatrix", items.get(i).model);
			
			modelItemObj.render(diffuseShaderFog);
		}
			
		diffuseShaderFog.end();
		
		        
		diffuseShaderFogWobble.begin();
		diffuseShaderFogWobble.setUniformMatrix("VPMatrix", cam.combined);
		diffuseShaderFogWobble.setUniformf("uFogColor", Resources.getInstance().fogColor[0],Resources.getInstance().fogColor[1],Resources.getInstance().fogColor[2],Resources.getInstance().fogColor[3]);
		
		// render all leafs
		for (int i = 0; i < leafs.size; ++i) {
			if(!cam.frustum.sphereInFrustum(leafs.get(i).collisionPosition,1)) continue;
			diffuseShaderFogWobble.setUniformf("time", startTime+leafs.get(i).randomStart);
			diffuseShaderFogWobble.setUniformMatrix("MMatrix", leafs.get(i).model);
			
			modelLeafObj.render(diffuseShaderFogWobble);
		}

		diffuseShaderFogWobble.end();
		
		
		diffuseShaderFogWobbleShark.begin();
		diffuseShaderFogWobbleShark.setUniformMatrix("VPMatrix", cam.combined);
		diffuseShaderFogWobbleShark.setUniformf("uFogColor", Resources.getInstance().fogColor[0],Resources.getInstance().fogColor[1],Resources.getInstance().fogColor[2],Resources.getInstance().fogColor[3]);
		
		// render all sharks
		modelSharkTex.bind(0);
		for (int i = 0; i < sharks.size; ++i) {
			if(!cam.frustum.sphereInFrustum(sharks.get(i).collisionPosition,10)) continue;

			diffuseShaderFogWobble.setUniformf("time", startTime+sharks.get(i).randomStart);
			diffuseShaderFogWobble.setUniformMatrix("MMatrix", sharks.get(i).model);
			
			modelSharkObj.render(diffuseShaderFogWobbleShark);
		}
		
		diffuseShaderFogWobbleShark.end();

	}

	private void collisionTest() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean keyDown(int keycode) {
		if (Gdx.input.isTouched())
			finished = true;
		if (keycode == Input.Keys.ESCAPE) {
			Gdx.app.exit();
		}
		if (keycode == Input.Keys.F) {
			if(Gdx.app.getType() == ApplicationType.Desktop) {
				if(!org.lwjgl.opengl.Display.isFullscreen()) {
					Gdx.graphics.setDisplayMode(Gdx.graphics.getDesktopDisplayMode().width, Gdx.graphics.getDesktopDisplayMode().height, true);
					Configuration.getInstance().setFullscreen(true);
				} else {
					Gdx.graphics.setDisplayMode(800,480, false);		
					Configuration.getInstance().setFullscreen(false);
				}				
			}
		}

		if (keycode == Input.Keys.F8) {
			try {
				ScreenshotSaver.saveScreenshot("screenshot");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (keycode == Input.Keys.PLUS) {
			Configuration.getInstance().setBrighness(Math.min(0.2f, Configuration.getInstance().brighness +0.01f));
		}
		
		if (keycode == Input.Keys.MINUS) {
			Configuration.getInstance().setBrighness(Math.max(0, Configuration.getInstance().brighness -0.01f));
		}

		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		x = (int) (x / (float) Gdx.graphics.getWidth() * 800);
		y = (int) (y / (float) Gdx.graphics.getHeight() * 480);
		finished = true;
		return false;
	}

	protected int lastTouchX;
	protected int lastTouchY;

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		x = (int) (x / (float) Gdx.graphics.getWidth() * 800);
		y = (int) (y / (float) Gdx.graphics.getHeight() * 480);
		return false;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		x = (int) (x / (float) Gdx.graphics.getWidth() * 800);
		y = (int) (y / (float) Gdx.graphics.getHeight() * 480);
		return false;
	}

	@Override
	public boolean touchMoved(int x, int y) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
