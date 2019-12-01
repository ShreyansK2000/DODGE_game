import java.awt.*;
import java.awt.image.BufferStrategy;
import java.io.Serializable;
import java.util.Random;

public class Game extends Canvas implements Runnable,Serializable{
    private static final long serialVersionUID = 2733916649114610736L;

    public static final int WIDTH = 640, HEIGHT = WIDTH/12*9;
    private Thread thread;
    private boolean running = false;

    private Random r;
    private Handler handler;
    private HUD hud;
    private Spawn spawner;
    private Menu menu;
    private GameState gstate;

    public enum STATE{
        Menu,
        FirstStage,
        SecondStage

    }
    public static STATE gameState = STATE.Menu;

    public Game(){
        handler = new Handler();
        menu = new Menu(this, handler);
        gstate = new GameState(handler);
        this.addKeyListener(new KeyInput(handler));
        this.addMouseListener(menu);
        new Window(WIDTH,HEIGHT,"Aoi no Sora",this);

        hud = new HUD();
        spawner = new Spawn(handler, hud);
        r = new Random();
        /*for (int i=0; i<50; i++){
            handler.addObject(new Player(r.nextInt(WIDTH),(r.nextInt(HEIGHT)),ID.Player));
        }*/
//        if (gameState == STATE.FirstStage) {
//            handler.addObject(new Player(100, 100, ID.Player, handler));
//            for (int i = 0; i < 5; i++) {
//                for (int j = 1; j < 10; j++) {
//                    handler.addObject(new Basic_Enemy(600, 50 * j, ID.Basic_Enemy));
//                }
//            }
//        }
    }
    public synchronized void start(){
        thread = new Thread(this);
        thread.start();

        running = true;
    }
    public synchronized void stop(){
        try{
            thread.join();
            running = false;
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public void run(){
        long lastTime = System.nanoTime();
        double amountofTicks = 60.0;
        double ns = 1000000000/amountofTicks;
        double delta =0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        this.requestFocus();
        while(running){
            long now = System.nanoTime();
            delta+=(now-lastTime)/ns;
            lastTime=now;
            while(delta>=1){
                tick();
                delta--;
            }
            if(running){
                render();
            }
            frames++;
            if(System.currentTimeMillis()-timer>1000){
                timer+=1000;
                //System.out.println("FPS: "+ frames);

                frames =0;
            }
        }
        stop();
    }
    private void tick(){
        handler.tick();
        if(gameState == STATE.FirstStage) {
            hud.tick();
            gstate.tick();
           // spawner.tick();

        }
        else if (gameState == STATE.Menu){
            menu.tick();
        }

    }
    private void render(){
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null){
            this.createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();

        g.setColor(Color.black);
        g.fillRect(0,0,WIDTH,HEIGHT);

        handler.render(g);
        if (gameState == STATE.FirstStage) {
            hud.render(g);
            gstate.render(g);
        }
        else if (gameState == STATE.Menu){
            menu.render(g);
        }
        g.dispose();
        bs.show();
    }
    public static void main(String args[]){
        new Game();
    }
}