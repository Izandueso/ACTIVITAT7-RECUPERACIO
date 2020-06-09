import java.awt.*;

import java.util.*;

import javax.swing.*;

import java.awt.event.*;



public class NauEspai extends javax.swing.JFrame {
        
	public NauEspai() {
		initComponents();
	}

	@SuppressWarnings("unchecked")

	private void initComponents() {

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setBackground(new java.awt.Color(255, 255, 255));
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 400, Short.MAX_VALUE));
		layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 300, Short.MAX_VALUE));
		pack();
	}

	public static void main(String args[]) {

		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception ex) {
			java.util.logging.Logger.getLogger(NauEspai.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		}

		NauEspai f = new NauEspai();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setTitle("Naus Espaials");
		f.setContentPane(new PanelNau());
		f.setSize(480, 560);
		f.setVisible(true);
	}
}

class PanelNau extends JPanel implements Runnable, KeyListener {

	//comptador amb les naus que hem matat
	public int navesMuertas = 0;
	private int numNaves = 10;
	Nau[] nau;
	Nau nauJugador;
        //boolea per comprobar si ja hem disparat
	public boolean disparado = false;
         private int numDisparo = 1; 

        //En el vector anirem guardant els tirs de la nau per poder seguir disparant
	public Vector<disparo> vectorDisparos = new Vector<disparo>(0);

	public PanelNau() {

		nau = new Nau[numNaves];

		for (int i = 0; i < nau.length; i++) {
			Random rand = new Random();
			int velocitat = (rand.nextInt(3) + 5) * 10;
			int posX = rand.nextInt(100) + 30;
			int posY = rand.nextInt(100) + 30;
			int dX = rand.nextInt(3) + 1;
			int dY = rand.nextInt(3) + 1;
			String nomNau = Integer.toString(i);
			nau[i] = new Nau(nomNau, posX, posY, dX, dY, velocitat, false);
		}
		//Creem el objecte nau del jugador
		nauJugador = new Nau("NauJugador", 200, 400, 10, 0, 100, false);
		
                //Creem un fil en el cual cada 0.1 segons anirem pintant per pantalla, i li fem start 
		Thread fil = new Thread(this);
		fil.start();

		//listener per les tecles que presionem 
                //listener per a que el fil principal gestioni
		addKeyListener(this);
		setFocusable(true);
	}

	public void run() {
		System.out.println("Comença fil per repintar");
		while (true) {
			try {
                                //esperem 0.1 segons
				Thread.sleep(100);
			} catch (Exception e) {
			} 
			System.out.println("dibuixant frames"); 
			repaint();
		}
	}

	public void paintComponent(Graphics g) {

		super.paintComponent(g);
                //si no ens han matat i encara no s'ha acabat la partida, continuem amb el joc
		if (nauJugador.destruida == false && navesMuertas != numNaves) {

			for (int i = 0; i < nau.length; ++i) {

				//si ens choquem amb alguna nau, morim
				if ((nauJugador.getY() > (nau[i].getY()) && nauJugador.getY() < (nau[i].getY() + 100))
						&& (nauJugador.getX() > (nau[i].getX())) && nauJugador.getX() < (nau[i].getX() + 60)
						&& nau[i].destruida == false) {

					nauJugador.pararRun();
					nauJugador.destruida = true;

					//si encara no em mort, pintem la nostra nau
				} else if (nauJugador.destruida == false) {
					nauJugador.pinta(g);
				}

				//si la nau enemiga no esta morta la pintem
				if (nau[i].destruida == false) {
					nau[i].pinta(g);
				}

                                //Si em disparat
				if (disparado) {
					for (int y = 0; y < vectorDisparos.size(); y++) {
                                                //pintem el dispar
						vectorDisparos.get(y).pinta(g);

						//si impactem amb el dispar a una nau enemiga
						if ((vectorDisparos.get(y).getY() > (nau[i].getY())
								&& vectorDisparos.get(y).getY() < (nau[i].getY() + 100))
								&& (vectorDisparos.get(y).getX() > (nau[i].getX()))
								&& vectorDisparos.get(y).getX() < (nau[i].getX() + 60) && nau[i].destruida == false) {

							// la nau enemiga es destruira
							nau[i].pararRun();
							nau[i].destruida = true;

							// actualitzem el comptador de naus mortes
							navesMuertas++;

                                                        //el dispar es destruira
							vectorDisparos.get(y).pararRun();
							vectorDisparos.remove(y);
                                                        
                                                //si el dispar surt del area de visio es destruira el objecte 
						} else if (vectorDisparos.get(y).getY() < 0) {
							vectorDisparos.get(y).pararRun();
							vectorDisparos.remove(y);
						}
					}
				}
			}
	
                //Sino, sortim del joc
		} else {
			System.exit(0);
		}
	}
	
        //metode per controlar el les tecles presionades
	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
                //Esquerra
		if (e.getKeyCode() == 37) {
			nauJugador.esquerra();
		} 
                //Dreta
		if (e.getKeyCode() == 39) {
			nauJugador.dreta();
		} 
                //espai (disparem)
		if (e.getKeyCode() == 32) {
			vectorDisparos.add(nauJugador.Disparo(nauJugador.getX() + 28, nauJugador.getY(), 0, -10, 100));
			// diem que hem disparat al boolea
			disparado = true;
                        //Anem dient quin dispar en concret es
                        System.out.println("Disparo numero " + numDisparo);
                        numDisparo++;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}
}

class Nau extends Thread {

	//boolea per gestionar el metode run (si es true funcionara , si es false no)
	private boolean partidaEnCurso = true;
	private String nomNau;
	private int x, y;
	private int dsx, dsy, v;
	private int tx = 10;
	private int ty = 10;

	// boolea per controlar si la nostra nau esta viva
	public boolean destruida;
	private Image image;

	public Nau(String nomNau, int x, int y, int dsx, int dsy, int v, boolean destruida) {
		this.nomNau = nomNau;
		this.x = x;
		this.y = y;
		this.dsx = dsx;
		this.dsy = dsy;
		this.v = v;
		this.destruida = destruida;
		image = new ImageIcon(Nau.class.getResource("nau.png ")).getImage();
                //fil de la nau
		Thread t = new Thread(this);
		t.start();  
	}

        //cada cop que es cridi a aquest metode es creara un objecte dispar
	public disparo Disparo(int x, int y, int dsx, int dsy, int v) {
		disparo disparo = new disparo(x, y, dsx, dsy, v);
		disparo.start();
		return disparo;
	}

	public int velocitat() {
		return v;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
        
        //metode per moure la nau
	public void moure() {
		x = x + dsx;
		y = y + dsy;
                
		//si arribem als limits de les franges establertes
		if (x >= 450 - tx || x <= tx){
                    dsx = -dsx;
                }	
		if (y >= 500 - ty || y <= ty){
                    dsy = -dsy;
                }	
	}

        //metode per pintar
	public void pinta(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(this.image, x, y, null);
	}

        //cuan no seguir sigui false deixara de funcionar el joc
	public void run() {
		while (partidaEnCurso) {
			try {
				Thread.sleep(this.v);
			} catch (Exception e) {
			}
			moure();
		}
	}

        //Metode que finalitzara el joc
	public void pararRun() {
		partidaEnCurso = false;
	}

	public void esquerra() {
		this.dsx = -10;
	}

	public void dreta() {
		this.dsx = 10;
	}
}

//classe dispar creada a partir de la clase nau
class disparo extends Thread {
	// metode per controlar si seguim jugant
	private boolean seguir = true;
	private String nom;
	private int x, y;
	private int dsx, dsy, v;
	private int tx = 10;
	private int ty = 10;
	Thread y1;
	private Image image;

	public disparo(int x, int y, int dsx, int dsy, int v) {
		this.x = x;
		this.y = y;
		this.dsx = dsx;
		this.dsy = dsy;
		this.v = v;
		image = new ImageIcon(Nau.class.getResource("dispar.png")).getImage();
		y1 = new Thread(this);
		y1.start();
	}

	public int velocitat() {
		return v;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void moure() {
		x = x + dsx;
		y = y + dsy;
	}

        //metode per pintar els dispars
	public void pinta(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(this.image, x, y, null);
	}

        //run que controlara els dispars
	public void run() {
		while (seguir) {
			try {
				Thread.sleep(this.v);
			} catch (Exception e) {
			}
			moure();
		}
	}

	//metode per parar el run dels dispars
	public void pararRun() {
		seguir = false;
	}
}