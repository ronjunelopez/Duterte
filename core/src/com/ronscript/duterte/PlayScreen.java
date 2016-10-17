package com.ronscript.duterte;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ronscript.duterte.utils.Constants;

/**
 * @author Ron
 * @since 8/8/2016
 */
public class PlayScreen implements Screen {

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;

    private GameWorld gameWorld;
    private GameInputProcessor gameInputProcessor;
    private Stage gui, g;
    private Viewport guiViewport, gViewport;

    private InputMultiplexer multiplexer;

    public PlayScreen(SpriteBatch batch) {
        this.batch = batch;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(Constants.WIDTH, Constants.HEIGHT, camera);
        camera.translate(Constants.WIDTH / 2, Constants.HEIGHT/  2);
        camera.update();


        gameInputProcessor = new GameInputProcessor();
        gameWorld = new GameWorld(batch, camera, viewport, gameInputProcessor);
        gameWorld.build();

        guiViewport = new FitViewport(Constants.WIDTH * 32, Constants.HEIGHT * 32, new OrthographicCamera());
        gViewport = new FitViewport(Constants.WIDTH, Constants.HEIGHT, new OrthographicCamera());
        gui = new Stage(guiViewport, batch);
        g = new Stage(guiViewport, batch);
//        camera.zoom = 0.5f;

        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(gui);
        multiplexer.addProcessor(gameInputProcessor);
        createUi();
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void createUi() {
        Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));

        CheckBox debugCheckbox = new CheckBox(" debug", skin);
//        debugCheckbox.setSize(1, 1);
//        debugCheckbox.setPosition(100 * unitScale, 20 * unitScale);
        debugCheckbox.addListener(new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y) {
                CheckBox checkBox = (CheckBox)event.getListenerActor();
                gameWorld.debug = checkBox.isChecked();
            }
        });

//        Label debugLabel = new Label("", new Label.LabelStyle(new BitmapFont(), Color.BLACK));
////        debugLabel.setPosition(100* unitScale, 60* unitScale);
////        stage.addActor(debugLabel);
//
//        Slider debugSlider = new Slider(0, 10, 0.1f, false, skin);
//        debugSlider.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                Slider slider = (Slider)actor;
//                float launchHeight = slider.getValue();
////                Gdx.app.log("Slider", "" + launchHeight);
////                gameWorld.getEngine().getSystem(WeaponSystem.class).launchHeight = launchHeight;
//            }
//        });

//        Touchpad touchpad = new Touchpad(10, skin);
//        List<Label> labelList = new List<Label>(skin);
//        labelList.setItems(debugLabel, debugLabel);
        Window window = new Window("Systems Debugger", skin);
//        window.set
//        window.addListener(new InputListener() {
//            @Override
//            public boolean keyDown(InputEvent event, int keycode) {
//                if(keycode == Input.Keys.SPACE) {
//                    if(event.getListenerActor().isVisible()) {
//                        event.getListenerActor().setVisible(false);
//                    } else {
//                        event.getListenerActor().setVisible(true);
//                    }
//                    return true;
//                }
//                return false;
//            }
//        });
//

        window.add(new ImageButton(skin));
        window.row();
        window.add(new Window("daw", skin));
        window.row();
        window.add(new Window("daw", skin));
        window.row();
        window.add(debugCheckbox);
//        window.add(touchpad);
//        window.row();
//        window.add(labelList);
//        window.row();
//        window.add(debugCheckbox);
//        window.row();
//        window.add(debugSlider);
//        window.scaleBy(1.5f);

        final Dialog dialog = new Dialog("Warning", skin, "dialog") {
            public void result(Object obj) {
                System.out.println("result "+obj);
            }
        };
        dialog.text("Are you sure you want to quit?");
        dialog.button("Yes", true); //sends "true" as the result
        dialog.button("No", false);  //sends "false" as the result
        dialog.key(Input.Keys.ENTER, true); //sends "true" when the ENTER key is pressed


//        final Tree tree = new Tree(skin);
//        final Node globalSetting = new Node(debugCheckbox);

        final Node gameObjectSystemsNode = new Node(new Label("Game Object Systems", skin));
        final Node visionSystemNode = new Node(new Label("Vision System", skin));
        visionSystemNode.getActor().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialog.show(gui);
            }
        });

//        visionSystemNode.add(new Node());

        gameObjectSystemsNode.add(visionSystemNode);

//        tree.add(globalSetting);
//        tree.add(gameObjectSystemsNode);


        gui.addActor(window);

        Touchpad touchpad = new Touchpad(20, skin);
        touchpad.setBounds(15, 15, 100, 100);
        gui.addActor(touchpad);



        // game related
//        gameWorld.getEngine().getEntitiesFor(Family.all(HealthComponent.class).get());
//        g.addActor();
    }

    private void handleInput() {

        boolean hasAccelerometer = Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer);

        if(hasAccelerometer) {
            Gdx.app.log("", Gdx.input.getAccelerometerX()+"");
        }

    }


    @Override
    public void render(float delta) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glClearColor(0,0,0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleInput();

        camera.update();
        viewport.apply();
        gameWorld.update(delta);

//        stage.getBatch().setProjectionMatrix(stage.getCamera().combined);
        g.draw();
        g.act(delta);
        gui.draw();
        gui.act(delta);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        gViewport.update(width, height, true);
        guiViewport.update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
