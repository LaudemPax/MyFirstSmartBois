package cc.sudocode.firstsmartbois;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class MainGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	OrthographicCamera camera;
	private Util ctrl;
	private Population popoulation;

	private TiledMap map;
	private TiledMapRenderer mapRenderer;

	public static Vector2 spawnPoint;

	//Box2d
	private World world;
	private Box2DDebugRenderer b2dRenderer;

	//Collision Bitmasks
	public static final int BOUNDARY = 0x0001;
	public static final int CAR = 0x0002;

	//text on screen
	BitmapFont font;

	//toggle debug render
	boolean debugRender = false;

	@Override
	public void create () {

		//initiate Box2d (for collision detection)
		Box2D.init();
		world = new World(new Vector2(0,0), true);

		//collision
		world.setContactListener(new ContactListener() {
			@Override
			public void beginContact(Contact contact) {

				//if neither of them are integers (and by extension not checkpoints)
				if(!(contact.getFixtureB().getBody().getUserData() instanceof Integer)  && !(contact.getFixtureA().getBody().getUserData() instanceof Integer)) {

					//stop the cat
                    if(contact.getFixtureA().getBody().getUserData() instanceof DriverUserData){
                    	((DriverUserData) contact.getFixtureA().getBody().getUserData()).stopped = true;

					}else if(contact.getFixtureB().getBody().getUserData() instanceof  DriverUserData){
						((DriverUserData) contact.getFixtureB().getBody().getUserData()).stopped = true;
					}
				}else{
					//add id to the car's checkpoints
					if(contact.getFixtureA().getBody().getUserData() instanceof DriverUserData){

						//if A is the car
                        int checkpointId = (Integer) contact.getFixtureB().getBody().getUserData();
                        DriverUserData driverData = ((DriverUserData) contact.getFixtureA().getBody().getUserData());

                        //check if the checkpoint is the lap marker
						if(checkpointId == Util.lapMarkerID){
							driverData.crossLapMarker();
						}else if(!driverData.hasCheckpont(checkpointId)){

							//if not the lap marker and if the car has not crossed the checkpoint
							driverData.addCheckpoint(checkpointId);
						}

					}else if(contact.getFixtureB().getBody().getUserData() instanceof  DriverUserData){

						//if B is the car
						int checkpointId = (Integer) contact.getFixtureA().getBody().getUserData();
						DriverUserData driverData = ((DriverUserData) contact.getFixtureB().getBody().getUserData());

						//check if the checkpoint is the lap marker
						if(checkpointId == Util.lapMarkerID){
							driverData.crossLapMarker();
						}else if(!driverData.hasCheckpont(checkpointId)){

							//if not the lap marker and if the car has not crossed the checkpoint
							driverData.addCheckpoint(checkpointId);
						}

					}

				}
			}

			@Override
			public void endContact(Contact contact) {

			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {

			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {

			}
		});

		b2dRenderer = new Box2DDebugRenderer();

		ctrl = new Util();
		map = new TmxMapLoader().load("track_1.tmx");

		mapRenderer = new OrthogonalTiledMapRenderer(map);

		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		//reposition and zoom camera
		camera.zoom = 1.8f;
		camera.position.set(700,940,camera.position.z);

		spawnPoint = new Vector2();


		setupMapObjects(world);

		popoulation = new Population(100,world,spawnPoint.x,spawnPoint.y);

		font =  new BitmapFont();
		font.getData().setScale(2.5f);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.52f, 0.152f, 0.219f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		mapRenderer.setView(camera);
		mapRenderer.render();


		if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
			debugRender = !debugRender;
		}

	    popoulation.step(Gdx.graphics.getDeltaTime());


		//calculate coordinates for text
		float camx = camera.position.x + camera.viewportWidth/2 - 200;
		float camy = camera.position.y + camera.viewportHeight/2 + 280;
		String gen = "Generation: " + popoulation.getGeneration();
		String bestFitness = "Fitness of best driver: " + popoulation.getFitnessOfBestDriver();
		String stepsTaken  = "Steps: " + popoulation.getStepsTakenByBestDriver();
		String laps = "Laps: " + popoulation.getLaps();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		popoulation.draw(batch);

		font.draw(batch,gen,camx,camy);
		font.draw(batch,bestFitness,camx,camy - 40);
		font.draw(batch,stepsTaken,camx,camy - 80);
		font.draw(batch,laps,camx,camy - 120);

		batch.end();

		if(debugRender)
			b2dRenderer.render(world, camera.combined);

		world.step(1/60f, 6, 2);
	}

	//load TMXMap stuff
	private void setupMapObjects(World world){

		MapLayer objectLayer = map.getLayers().get(1);
		MapObjects objects = objectLayer.getObjects();

		for(MapObject obj : objects){

			float x = 0;
			float y = 0;

			if(obj.getName() != null){

				x = (float) obj.getProperties().get("x");
				y = (float) obj.getProperties().get("y");

			}

			if(obj.getName() != null && obj.getName().equals("spawn")){
				System.out.println("Spawn point set");
				spawnPoint.set(x,y);
			}

			//without the check for a null object name, even points are considered RectangleMapObjects
			if(obj instanceof RectangleMapObject && obj.getName() == null){


				BodyDef bdef = new BodyDef();
				bdef.type = BodyDef.BodyType.StaticBody;

				Body body = world.createBody(bdef);
				FixtureDef fdef = new FixtureDef();
				fdef.filter.categoryBits = BOUNDARY;
				fdef.filter.maskBits = CAR;
				fdef.shape = getRectangle((RectangleMapObject) obj);
				body.createFixture(fdef);

			}

		}

		setupCheckpoints(world);

	}

	//generates checkpoint rectangle objects
	private void setupCheckpoints(World world){

		int checkpointID = 1000;

		MapLayer checkpointsLayer = map.getLayers().get(2);
		MapObjects objects = checkpointsLayer.getObjects();

		for(MapObject obj : objects) {

			if(obj instanceof RectangleMapObject){


				BodyDef bdef = new BodyDef();
				bdef.type = BodyDef.BodyType.StaticBody;

				Body body = world.createBody(bdef);
				FixtureDef fdef = new FixtureDef();
				fdef.filter.categoryBits = BOUNDARY;
				fdef.filter.maskBits = CAR;

				float x = (float) obj.getProperties().get("x");
				float y = (float) obj.getProperties().get("y");
				float width = (float) obj.getProperties().get("width");
				float height = (float) obj.getProperties().get("height");

				float angle = 0;
				Vector2 center;

				//math wizardry for the angled checkpoints
				if(obj.getProperties().containsKey("rotation")){
					angle = (float) obj.getProperties().get("rotation");
					angle = Util.degToRad(angle); //change to radians

					//original angle as in Tiled (Top left corner of rectangle)
					center = new Vector2(x,y + height);

					float theta = Util.degToRad(90) - Math.abs(angle);

					//coordinate adjust
					float dx = (float) (height/2 * Math.cos(theta));
					float dy = (float) (height/2 * Math.sin(theta));

					//this is due to the geometry of angled rectangles
					if(angle > 0){
						center.x -= dx;
						center.y -= dy;
					}else{
						center.x += dx;
						center.y -= dy;
					}

					//negate the angle (Tiled -> Libgdx)
					angle = -angle;

				}else{
					center = new Vector2(x+width/2,y+height/2);
					angle = 0;
				}
				PolygonShape shape = new PolygonShape();

				shape.setAsBox(width/2,height/2, center, angle);

				fdef.shape = shape;

				body.createFixture(fdef);

				//to ensure car only goes past the same checkpoint once
				if(obj.getName() != null && obj.getName().equals("lap")){
					System.out.println("Lap Marker set");
					body.setUserData(Util.lapMarkerID);
				}else{
					body.setUserData(checkpointID);
					checkpointID++;
				}

			}

		}

	}

	private PolygonShape getRectangle(RectangleMapObject rectangleObject) {
		Rectangle rectangle = rectangleObject.getRectangle();
		PolygonShape polygon = new PolygonShape();
		Vector2 size = new Vector2((rectangle.x + rectangle.width * 0.5f),
				(rectangle.y + rectangle.height * 0.5f ));

		polygon.setAsBox(rectangle.width * 0.5f,
				rectangle.height * 0.5f ,
				size,
				0);

		return polygon;
	}
	@Override
	public void dispose () {
		batch.dispose();
	}


}
