package de.swagner.ld22;

import java.io.IOException;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.loaders.ModelLoaderRegistry;
import com.badlogic.gdx.graphics.g3d.model.still.StillModel;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;


public class GameScreen extends DefaultScreen implements InputProcessor {

	float startTime = 0;
	PerspectiveCamera cam;
	Frustum camCulling = new Frustum();
	
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
		
	Music breath;
	Music fastBreath;
	Music music;
	Sound pickUp;
	Sound danger;
	Sound getBite;
	Sound noBreath;
	Sound footStep;

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

	Player player = new Player();

	float scale = 1.0f;
	float rotate = 1.0f;
	
	int collectedItems = 0;
	float oxygenLevel = 100.f;
	double speed = 3;

	float fade = 1.0f;
	boolean finished = false;
	
	float footStepCnt = 0;

	float delta;

	// GLES20
	Matrix4 model = new Matrix4().idt();
	Matrix4 normal = new Matrix4().idt();
	Matrix4 tmp = new Matrix4().idt();

	private ShaderProgram diffuseShader;
	private ShaderProgram diffuseShaderFog;
	private ShaderProgram diffuseShaderFogWobble;
	private ShaderProgram diffuseShaderFogWobbleShark;
	private boolean dangerPlays = false;
	private boolean win = false;
	
	private boolean dead = false;
	private boolean playDead2 = false;
	float deadAnimTime = 0;
	float deadAnimTime2 = 0;

	public GameScreen(Game game) {
		super(game);
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setInputProcessor(this);
		
		if (Gdx.app.getType() == ApplicationType.Desktop || Gdx.app.getType() == ApplicationType.Applet) {
			Gdx.input.setCursorCatched(true);
			Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
			lastTouchX = Gdx.graphics.getWidth() / 2;
			lastTouchY = Gdx.graphics.getHeight() / 2;
		}
		
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

		breath = Resources.getInstance().breath;
		breath.setLooping(true);
		breath.play();		
		
		fastBreath = Resources.getInstance().fastBreath;
		fastBreath.setLooping(true);
		
		music = Resources.getInstance().intro;
		music.setVolume(0.4f);
		music.setLooping(true);
		music.play();		
		
		pickUp = Resources.getInstance().pickUp;
		danger = Resources.getInstance().danger;
		noBreath = Resources.getInstance().noBreath;
		getBite = Resources.getInstance().getBite;
		footStep = Resources.getInstance().footStep;
		
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
		Vector3 oldPosition = new Vector3();
		Vector3 oldDirection = new Vector3();
		if(cam!=null) {
			oldPosition.set(cam.position);
			oldDirection.set(cam.direction);
			cam = new PerspectiveCamera(45, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			cam.position.set(oldPosition);
			cam.direction.set(oldDirection);
		} else {
			cam = new PerspectiveCamera(45, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			cam.position.set(0, 0f, 16f);
			cam.direction.set(0, 0, -1);
		}
		cam.up.set(0, 1, 0);
		cam.near = 0.5f;
		cam.far = 600f;
		
		if (Gdx.app.getType() == ApplicationType.Desktop) {
			Gdx.input.setCursorCatched(true);
			Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
			lastTouchX = Gdx.graphics.getWidth() / 2;
			lastTouchY = Gdx.graphics.getHeight() / 2;
		}
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
		
		for (int i = 0; i < 2; i++) {
			position = new Vector3();
			boolean add = false;
			while(!add) {
				position = new Vector3(MathUtils.random(-100, 100), 6, MathUtils.random(-100, 100));
				add = true;
				if((position.x < 80 && position.x > -80) 
						|| (position.z < 80  && position.z > -80)) {
					add = false;
//					System.out.println("no sharks allowd here: " + position);
				}
			}
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
			leafs.add(new Leaf(position, MathUtils.random(10, 30)/100.f , MathUtils.random(0,
					8), MathUtils.random(0, 360)));
		}
		
		for (int i = 0; i < 15; i++) {
			position = new Vector3();
			boolean add = false;
			while(!add) {
				position = new Vector3(MathUtils.random(-100, 100), -1, MathUtils.random(-100, 100));
				add = true;
				for (int n = 0; n< stones.size; n++) {
					if((stones.get(n).position.x <= position.x+3 && stones.get(n).position.x >= position.x-3) 
							&& (stones.get(n).position.z <= position.z+3 && stones.get(n).position.z >= position.z-3)) {
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
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		delta = Math.min(0.1f, deltaTime);

		startTime += delta;
		
		scale += (MathUtils.sin(startTime) * delta)/20.f;
		rotate += (MathUtils.cos(startTime) * delta)/10.f;

		if (Gdx.app.getType() == ApplicationType.Android) {
			if (Gdx.input.justTouched()) {
				lastTouchX = Gdx.input.getX();
				lastTouchY = Gdx.input.getY();
			} else if (Gdx.input.isTouched()) {
				cam.rotate(0.2f * (lastTouchX - Gdx.input.getX()), 0.0f, 1.0f, 0.0f);
				// cam.rotate(0.2f * (lastTouchY - Gdx.input.getY()), 1.0f,
				// 0.0f, 0.0f);
				cam.direction.y += 0.01f * (lastTouchY - Gdx.input.getY());
				// cam.direction.x -= 0.01f * (lastTouchX - Gdx.input.getX());
				lastTouchX = Gdx.input.getX();
				lastTouchY = Gdx.input.getY();
			}
		}

		processInput();
		if(dead) {
			playDeadAnim();
		}
		
		cam.update();

		collisionTest();		
		updateAI();
		
		renderScene();
		
		batch.begin();
			font.draw(batch, "Collected Gold: " + collectedItems + "/10" ,20, 30);
			if(MathUtils.round(oxygenLevel)>0) {
				font.draw(batch, "Oxgen: " + MathUtils.round(oxygenLevel) ,20, 50);
			} else {
				if(dead==false) {
					noBreath.play();
    			}
				font.draw(batch, "System offline - Dead" ,20, 50);
				dead = true;
			}
			
			if(collectedItems>=10) {
				font.draw(batch, "You got enough gold to feed your wife and children! Return to the submarine!" ,20, 460);
			}
			
			//near submarine
			if(submarine.position.dst(cam.position)<30) {
				if(collectedItems>=10) {
					win  = true;
					finished = true;
				}
				if(startTime> 10) {
					font.draw(batch, "You still need " + (10-collectedItems) + " gold before you can leave!" ,20, 460);
				}
			}			
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
				breath.stop();
				fastBreath.stop();
				music.stop();
				game.setScreen(new WinScreen(game,win));
			}
		}
	}

	private void playDeadAnim() {
		breath.stop();
		win = false;
		deadAnimTime += Gdx.graphics.getDeltaTime();
		cam.up.x = MathUtils.sin(deadAnimTime*5.0f);
		if(cam.direction.y<0.9f && !playDead2 ) {
			cam.direction.y += Gdx.graphics.getDeltaTime();
		} else {
			playDead2 = true;
			deadAnimTime2 += Gdx.graphics.getDeltaTime();
			cam.direction.y = MathUtils.cos(deadAnimTime2*1.0f);
		}
		if(deadAnimTime>=2) {
			finished = true;
		}
	}


	private void updateAI() {
		if(MathUtils.round(oxygenLevel)==70 && !dangerPlays ) {
			danger.play();
			dangerPlays = true;
		}
		if(MathUtils.round(oxygenLevel)==69) {
			dangerPlays = false;
		}
		if(MathUtils.round(oxygenLevel)==50 && !dangerPlays ) {
			danger.play();
			dangerPlays = true;
		}
		if(MathUtils.round(oxygenLevel)==49) {
			dangerPlays = false;
		}
		if(MathUtils.round(oxygenLevel)==40 && !dangerPlays ) {
			danger.play();
			dangerPlays = true;
		}
		if(MathUtils.round(oxygenLevel)==39) {
			dangerPlays = false;
		}
		if(MathUtils.round(oxygenLevel)==30 && !dangerPlays ) {
			danger.play();
			dangerPlays = true;
		}
		if(MathUtils.round(oxygenLevel)==29) {
			dangerPlays = false;
		}
		if(MathUtils.round(oxygenLevel)==20 && !dangerPlays ) {
			danger.play();
			dangerPlays = true;
		}
		if(MathUtils.round(oxygenLevel)==19) {
			dangerPlays = false;
		}
		if(MathUtils.round(oxygenLevel)==10 && !dangerPlays ) {
			danger.play();
			dangerPlays = true;
		}
		if(MathUtils.round(oxygenLevel)==9) {
			dangerPlays = false;
		}
		if(MathUtils.round(oxygenLevel)==5 && !dangerPlays ) {
			danger.play();
			dangerPlays = true;
		}
		if(MathUtils.round(oxygenLevel)==4) {
			dangerPlays = false;
		}
		if(MathUtils.round(oxygenLevel)==3 && !dangerPlays ) {
			danger.play();
			dangerPlays = true;
		}
		if(MathUtils.round(oxygenLevel)==2) {
			dangerPlays = false;
		}
		if(MathUtils.round(oxygenLevel)==1 && !dangerPlays ) {
			danger.play();
			dangerPlays = true;
		}
		if(MathUtils.round(oxygenLevel)==0) {
			dangerPlays = false;
		}		
		
		//update sharks
		for (int i = 0; i < sharks.size; ++i) {
			sharks.get(i).update(cam.position,delta);
		}
	}


	private void renderScene() {

		diffuseShader.begin();
		diffuseShader.setUniformMatrix("VPMatrix", cam.projection.setToOrtho(0, 16, 0, 10, 1, 100));
		
		// render suit
		tmp.idt();
		model.idt();
		
		tmp.setToTranslation(1.75f+(MathUtils.cos(player.headMovement)/15.f),-1+(MathUtils.sin(player.headMovement)/8.f),-50);
		model.mul(tmp);
				
		tmp.setToRotation(Vector3.Z, 90);
		model.mul(tmp);
		
		tmp.setToRotation(Vector3.X, 90);
		model.mul(tmp);
				
		
		diffuseShader.setUniformMatrix("MMatrix", model);
		diffuseShader.setUniformi("uSampler", 0);
		
		modelSuitTex.bind(0);
		modelSuitObj.render(diffuseShader);
		
		diffuseShader.end();
		
		
		
		diffuseShaderFog.begin();
		diffuseShaderFog.setUniformMatrix("VPMatrix", cam.combined);
		diffuseShaderFog.setUniformf("uFogColor", Resources.getInstance().fogColor[0],Resources.getInstance().fogColor[1],Resources.getInstance().fogColor[2],Resources.getInstance().fogColor[3]);
		diffuseShaderFog.setUniformi("uSampler", 0);
		
		
		// render top
		tmp.idt();
		model.idt();

		tmp.setToTranslation(cam.position.x,30f, cam.position.z);
		model.mul(tmp);
		
		tmp.setToScaling(50,50,50);
		model.mul(tmp);
			
		diffuseShaderFog.setUniformMatrix("MMatrix", model);
		diffuseShaderFog.setUniformi("uSampler", 0);
		
		modelOverSeaTex.bind(0);
		modelPlaneObj.render(diffuseShaderFog);
		
		// render ground
		tmp.idt();
		model.idt();

		tmp.setToTranslation(18,-1.1f, 0);
		model.mul(tmp);
		
		tmp.setToScaling(1000,1000,1000);
		model.mul(tmp);
			
		diffuseShaderFog.setUniformMatrix("MMatrix", model);
		diffuseShaderFog.setUniformi("uSampler", 0);
		
		modelDownSeaTex.bind(0);
		modelPlaneObj.render(diffuseShaderFog);
		
		
		// render ship	
		if(cam.frustum.sphereInFrustum(submarine.collisionPosition,20)) {
			modelShipTex.bind(0);
			diffuseShaderFog.setUniformMatrix("MMatrix", submarine.model);
			modelShipObj.render(diffuseShaderFog);
		}
		
        
		// render all rocks
		modelWhiteTex.bind(0);
		for (int i = 0; i < stones.size; ++i) {
			if(!cam.frustum.sphereInFrustum(stones.get(i).collisionPosition,1)) continue;

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
		diffuseShaderFogWobble.setUniformf("time", startTime);
		
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
		diffuseShaderFogWobbleShark.setUniformf("time", startTime);
		
		// render all sharks
		modelSharkTex.bind(0);
		for (int i = 0; i < sharks.size; ++i) {
			if(!cam.frustum.sphereInFrustum(sharks.get(i).collisionPosition,10)) continue;

			diffuseShaderFogWobble.setUniformf("time", startTime+sharks.get(i).randomStart);
			diffuseShaderFogWobble.setUniformMatrix("MMatrix", sharks.get(i).model);
			
			modelSharkObj.render(diffuseShaderFogWobbleShark);
		}
		
		diffuseShaderFogWobbleShark.end();
		
		
		diffuseShader.begin();
		diffuseShader.setUniformMatrix("VPMatrix", cam.projection.setToOrtho(0, 23, 0, 16, 1, 100));

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_DST_COLOR);
		
		// render suit display
		tmp.idt();
		model.idt();
		
		tmp.setToTranslation(12.5f+(MathUtils.cos(player.headMovement)/20.f),8+(MathUtils.sin(player.headMovement)/10.f),-60);
		model.mul(tmp);
		
		tmp.setToScaling(10,10,10);
		model.mul(tmp);
		
		tmp.setToRotation(Vector3.X, 90);
		model.mul(tmp);		
				
		diffuseShader.setUniformMatrix("MMatrix", model);
		diffuseShader.setUniformi("uSampler", 0);
		
		modelLensTex.bind(0);
		modelPlaneObj.render(diffuseShader);
		
		Gdx.gl.glDisable(GL20.GL_BLEND);
		

		diffuseShader.end();


	}

	Vector3 movement = new Vector3();
    Vector3 intersection = new Vector3();
    Ray ray = new Ray(new Vector3(), new Vector3());
	private void processInput() {
		if(dead) return;
		
		movement.set(0, 0, 0);

		if (Gdx.input.isKeyPressed(Keys.W)) {
			movement.add(cam.direction.tmp().mul(Gdx.graphics.getDeltaTime()));
			movement.add(cam.up.tmp().mul(player.headMovement/400.f));
			if(player.headMovementDownUp) {
				player.headMovement = Math.max(-1, player.headMovement - (Gdx.graphics.getDeltaTime()*5));
				if(player.headMovement==-1) {
					player.headMovementDownUp = false;
				}
			} else {
				player.headMovement = Math.min(1, player.headMovement +  (Gdx.graphics.getDeltaTime()*5));
				if(player.headMovement==1) {
					player.headMovementDownUp = true;
				}
			}
		}

		if (Gdx.input.isKeyPressed(Keys.S)) {
			movement.add(cam.direction.tmp().mul(-Gdx.graphics.getDeltaTime()));
			movement.add(cam.up.tmp().mul(player.headMovement/400.f));
			if(player.headMovementDownUp) {
				player.headMovement = Math.max(-1, player.headMovement - (Gdx.graphics.getDeltaTime()*5));
				if(player.headMovement==-1) {
					player.headMovementDownUp = false;
				}
			} else {
				player.headMovement = Math.min(1, player.headMovement +  (Gdx.graphics.getDeltaTime()*5));
				if(player.headMovement==1) {
					player.headMovementDownUp = true;
				}
			}
		}

		if (Gdx.app.getType() == ApplicationType.Desktop || Gdx.app.getType() == ApplicationType.Applet) {

			if (Gdx.input.isKeyPressed(Keys.A)) {
				movement.add(cam.direction.tmp().crs(cam.up).mul(-Gdx.graphics.getDeltaTime()));
			}

			if (Gdx.input.isKeyPressed(Keys.D)) {
				movement.add(cam.direction.tmp().crs(cam.up).mul(Gdx.graphics.getDeltaTime()));
			}
			
			if (Gdx.input.isKeyPressed(Keys.LEFT)) {
				cam.rotate(50 * Gdx.graphics.getDeltaTime(), 0, 1, 0);
			}

			if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
				cam.rotate(-50 * Gdx.graphics.getDeltaTime(), 0, 1, 0);
			}
			
			if (Gdx.input.isKeyPressed(Keys.UP)) {
				cam.direction.y += Gdx.graphics.getDeltaTime();
			}

			if (Gdx.input.isKeyPressed(Keys.DOWN)) {
				cam.direction.y -= Gdx.graphics.getDeltaTime();
			}

		}
		if (Gdx.app.getType() == ApplicationType.Android) {
			if (Gdx.input.isKeyPressed(Keys.A)) {
				cam.rotate(50 * Gdx.graphics.getDeltaTime(), 0, 1, 0);
			}

			if (Gdx.input.isKeyPressed(Keys.D)) {
				cam.rotate(-50 * Gdx.graphics.getDeltaTime(), 0, 1, 0);
			}
		}

		movement.mul((float) speed);
		
		footStepCnt += movement.len();
		if(footStepCnt>3) {
			footStep.play(0.8f);
			footStepCnt = 0;
		}
		
		oxygenLevel -= ((Math.pow(speed,3.5f) * Gdx.graphics.getDeltaTime()/350.f));
		
		cam.position.add(movement);

        cam.position.y =  Math.max(2.0f,Math.min(2.7f, cam.position.y));
		
		
		ray.origin.set(cam.position);
		ray.origin.y = 0;
		Vector3 collisionDir = new Vector3(cam.direction.cpy());
		collisionDir.y = 0;		
        ray.direction.set(collisionDir);
        
        
        for (int i = 0; i < bigStones.size; ++i) {
			if(cam.frustum.sphereInFrustum(bigStones.get(i).collisionPosition,1000)) {				
        		if(ray.origin.dst(bigStones.get(i).collisionPosition)<=50) {
        			if(bigStones.get(i).collisionPosition.x>cam.position.x) {
        				movement.x += (bigStones.get(i).collisionPosition.x-ray.origin.x) * Gdx.graphics.getDeltaTime();
        			}
        			if(bigStones.get(i).collisionPosition.x<cam.position.x) {
        				movement.x -= (ray.origin.x-bigStones.get(i).collisionPosition.x) * Gdx.graphics.getDeltaTime();
        			}
                	cam.position.sub(movement);
                	break;
                }
			}
        }
                
        for (int i = 0; i < items.size; ++i) {
			if(cam.frustum.sphereInFrustum(items.get(i).collisionPosition,1000)) {				
        		if(ray.origin.dst(items.get(i).collisionPosition)<=3) {
//        			System.out.println("collected");
        			items.removeIndex(i);
        			collectedItems += 1;
        			pickUp.play(40);
                	break;
                }
			}
        }
               
		if(cam.frustum.sphereInFrustum(submarine.collisionPosition,1000)) {	
			if(ray.origin.dst(submarine.collisionPosition)<=16) {
    			if(submarine.collisionPosition.x>cam.position.x) {
    				movement.x += (submarine.collisionPosition.x-ray.origin.x) * Gdx.graphics.getDeltaTime();
    			}
    			if(submarine.collisionPosition.x<cam.position.x) {
    				movement.x -= (ray.origin.x-submarine.collisionPosition.x) * Gdx.graphics.getDeltaTime();
    			}
            	cam.position.sub(movement);
            }
		}
		
        for (int i = 0; i < sharks.size; ++i) {
			if(cam.frustum.sphereInFrustum(sharks.get(i).collisionPosition,1000)) {				
        		if(ray.origin.dst(sharks.get(i).collisionPosition)<=12) {
        			if(dead==false) {
            			getBite.play();
        			}
        			dead = true;        			
                	break;
                }
			}
        }
	        
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
			return false;
		if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACK) {
			finished = true;
		}
		if (keycode == Input.Keys.F12) {
			dead = true;
			win = true;
		}
		if (keycode == Input.Keys.F11) {
			dead = true;
			win = false;
		}
		if (keycode == Input.Keys.SHIFT_LEFT || keycode == Input.Keys.SHIFT_RIGHT) {
			if(speed == 3) {
				speed = 6;
				breath.stop();
				fastBreath.play();
			}
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
		if (keycode == Input.Keys.SHIFT_LEFT || keycode == Input.Keys.SHIFT_RIGHT) {
			if(speed == 6) {
				speed = 3;
				fastBreath.stop();
				breath.play();
			}
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		if(dead) return false;
		x = (int) (x / (float) Gdx.graphics.getWidth() * 800);
		y = (int) (y / (float) Gdx.graphics.getHeight() * 480);
		return false;
	}

	protected int lastTouchX;
	protected int lastTouchY;

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		if(dead) return false;
		x = (int) (x / (float) Gdx.graphics.getWidth() * 800);
		y = (int) (y / (float) Gdx.graphics.getHeight() * 480);
		return false;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		if(dead) return false;
		x = (int) (x / (float) Gdx.graphics.getWidth() * 800);
		y = (int) (y / (float) Gdx.graphics.getHeight() * 480);
		return false;
	}

	@Override
	public boolean touchMoved(int x, int y) {
		if(dead) return false;
		if (Gdx.app.getType() == ApplicationType.Desktop || Gdx.app.getType() == ApplicationType.Applet) {			
				cam.rotate(0.1f * (lastTouchX - Gdx.input.getX()), 0.0f, 1.0f, 0.0f);
				cam.direction.y = Math.max(-1.0f,Math.min(1.0f,cam.direction.y + 0.005f * (lastTouchY - Gdx.input.getY())));
				lastTouchX = Gdx.input.getX();
				lastTouchY = Gdx.input.getY();			
		}
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
