package com.marp.display;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.management.ManagementFactory;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import com.marp.maze.Maze;
import com.marp.travellers.MazeTraveller;
import com.marp.travellers.filters.Filters;
import com.marp.travellers.generation.Generators;
import com.marp.travellers.solving.Solvers;
import com.marp.util.ElementArray;
import com.marp.util.UTJTextField;

public class Window extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6499489497279601421L;
	private static final int DEFAULT_WIDTH = 31;
	private static final int DEFAULT_HEIGHT = 25;

	
	private final MazeArrayDisplay mad = new MazeArrayDisplay();
	private final ElementArray<Maze> ma = new ElementArray<Maze>();
	private final ElementArray<MazeTraveller> gen = new ElementArray<MazeTraveller>();
	private final ElementArray<MazeTraveller> sol = new ElementArray<MazeTraveller>();
	
	private final ElementArray<Generators> generators = new ElementArray<Generators>();
	private final ElementArray<String>	generatorConfigs = new ElementArray<String>();
	private final ElementArray<Solvers> solvers = new ElementArray<Solvers>();
	private final ElementArray<String>	solverConfigs = new ElementArray<String>();
	private final ElementArray<Integer> widths = new ElementArray<Integer>();
	private final ElementArray<Integer> heights = new ElementArray<Integer>();
	
	
	int width = DEFAULT_WIDTH;
	int height = DEFAULT_HEIGHT;
	private Generators currentGenerator = Generators.prim;
	private String currentGeneratorConfig = "interval='1',delete='0.2'";
	private Solvers currentSolver = Solvers.astar;
	private String currentSolverConfig = "interval='1'";
	private Filters currentFilter = Filters.whitenoise;
	private String currentFilterConfig = "prob='0.2',default='false',mode='path'";
	
	private JFrame window = this;
	
	
	public Window() {
		this.setSize(900,600);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.add(bottomMenuBar,BorderLayout.SOUTH);
		this.setJMenuBar(topMenuBar);
		this.add(mad);
		this.restartViews(1);
		this.setCurrentParamsToSelectedMazes();
		mad.startRepaintCicle(50);
		
		
		new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(200);
						setTitle(Integer.toString(ManagementFactory.getThreadMXBean().getThreadCount()));
						
					} catch (InterruptedException e) {}
				}
			}
		}.start();
		
		this.setVisible(true);
	}
	
	//RESET STUFF
	private void restartViews(int size) {
		//comprobar y terminar cosas en ejecución?
		int newSize = size;
		mad.setDisplayCount(newSize);
		ma.changeSize(newSize,null);
		gen.changeSize(newSize,null);
		sol.changeSize(newSize,null);
		generators.changeSize(newSize,currentGenerator);
		generatorConfigs.changeSize(newSize,currentGeneratorConfig);
		solvers.changeSize(newSize,currentSolver);
		solverConfigs.changeSize(newSize,currentSolverConfig);
		widths.changeSize(newSize,width);
		heights.changeSize(newSize,height);
		
		//necesario para que no haya laberintos nulos y empecemos siempre con algo en la vista
		this.fillAllNullMazes();
	}
	private void fillAllNullMazes() {
		for (int i = 0; i < ma.getSize(); i++) 
			if (ma.getElementAt(i) == null) 
				resetMazeAt(i,false);
	}
	private void fillAllSelectedMazes(Boolean fill) {
		for (int i = 0; i < mad.getDisplayCount(); i++)
			if (mad.isSelected(i))
				resetMazeAt(i,fill);
	}
	private void resetSelectedMazesColor() {
		for (int i = 0; i < ma.getSize() && i < mad.getDisplayCount(); i++)
			if (mad.isSelected(i))
				ma.getElementAt(i).initData();
	}
	//////
	
	//DEBUG INFO SETTERS
	private void setTData(boolean newValue) {
		mad.setTData(newValue);
	}
	private void setMData(boolean newValue) {
		mad.setMData(newValue);
	}
	private void setThinRender(boolean newValue) {
		mad.setThinRender(newValue);
	}
	private void setAdvRender(boolean newValue) {
		mad.setAdvRender(newValue);
	}
	//////
	
	//CURRENT CONFIG SETTERS
	private void setCurrentWidth(int newWidth) {
		if (newWidth <= 0)
			return;
		this.width = newWidth;
	}
	private void setCurrentHeight(int newHeight) {
		if (newHeight <= 0)
			return;
		this.height = newHeight;
	}
	private void setCurrentGenerator(Generators g) {
		this.currentGenerator = g;
	}
	private void setCurrentSolver(Solvers s) {
		this.currentSolver = s;
	}
	private void setCurrentFilter(Filters f) {
		this.currentFilter = f;
	}
	private void setCurrentGeneratorConfig(String s) {
		this.currentGeneratorConfig = s;
	}
	private void setCurrentSolverConfig(String s) {
		this.currentSolverConfig = s;
	}
	private void setCurrentFilterConfig(String s) {
		this.currentFilterConfig = s;
	}
	//////
	
	//SETTING PARAMETERS
	private void setCurrentParamsToSelectedMazes() {
		for (int i = 0; i < mad.getDisplayCount(); i++)
			if (mad.isSelected(i))
				setCurrentParams(i);
	}
	private void setCurrentParams(int index) {
		//Integer cw = widths.getElementAt(index), ch = heights.getElementAt(index);
		//if the dimensions didn't change don't remake the maze
		//if (cw != width || ch != height) 
		widths.setElementAt(width, index);
		heights.setElementAt(height, index);
		generators.setElementAt(currentGenerator, index);
		solvers.setElementAt(currentSolver, index);
		generatorConfigs.setElementAt(currentGeneratorConfig, index);
		solverConfigs.setElementAt(currentSolverConfig, index);
		
		mad.setExtraDataForDisplay(" GEN:" + currentGenerator.toString() + " SOL:" + currentSolver.toString() + "\nGENCFG:" + currentGeneratorConfig + "\nSOLCFG:" + currentSolverConfig, index);
		
		//change the traveller config "on the fly" if there is a generation or solving in process
		if (gen.getElementAt(index) != null)
			gen.getElementAt(index).setConfig(currentGeneratorConfig);
		if (sol.getElementAt(index) != null)
			sol.getElementAt(index).setConfig(currentSolverConfig);
	}
	//////

	//PLAYBACK CONTROLS
	private void playSelected() {
		for (int i = 0; i < mad.getDisplayCount(); i++)
			if (mad.isSelected(i))
				mad.pauseDisplayAt(i,false);
	}
	private void stopSelected() {
		for (int i = 0; i < mad.getDisplayCount(); i++)
			if (mad.isSelected(i))
				mad.pauseDisplayAt(i,true);
	}
	private void stepSelected() {
		for (int i = 0; i < mad.getDisplayCount(); i++)
			if (mad.isSelected(i))
				mad.stepAt(i);
	}
	//////

	//CREATION&SOLVING&RELATED
	/**
	 * Creates a new maze with the width and height stored at index i, and default value = startvalue.
	 * It also sets the new maze to be displayed at the appropiate location
	 * @param i
	 */
	private void resetMazeAt(int i, boolean startvalue) {
		Maze m = new Maze(widths.getElementAt(i),heights.getElementAt(i),startvalue);
		ma.setElementAt(m, i);
		mad.setMazeForDisplay(m, i);
	}
	/**
	 * Generates all currently selected mazes
	 */
	private void generateSelectedMazes() {
		for (int i = 0; i < mad.getDisplayCount(); i++)
			if (mad.isSelected(i))
				generateMazeAt(i);
	}
	/**
	 * Starts generation of the maze at index i
	 * @param i
	 */
	private void generateMazeAt(int i) {
		this.resetMazeAt(i, false);
		
		MazeTraveller mazeGenerator = Generators.getAssociatedGenerator(generators.getElementAt(i), ma.getElementAt(i));
		mazeGenerator.setConfig(generatorConfigs.getElementAt(i));
		gen.setElementAt(mazeGenerator,i);
		
		mad.setTravellerForDisplay(mazeGenerator, i);
		
		mazeGenerator.start();
	}
	/**
	 * Generates all currently selected mazes
	 */
	private void solveSelectedMazes() {
		for (int i = 0; i < mad.getDisplayCount(); i++)
			if (mad.isSelected(i))
				solveMazeAt(i);
	}
	/**
	 * Solves the maze at index i stopping any maze travellers for the maze at index i
	 * @param i
	 */
	private void solveMazeAt(int i) {
		MazeTraveller mazeSolver = Solvers.getAssociatedSolver(solvers.getElementAt(i), ma.getElementAt(i));
		mazeSolver.setConfig(solverConfigs.getElementAt(i));
		sol.setElementAt(mazeSolver,i);
		
		mad.setTravellerForDisplay(mazeSolver, i);
		mazeSolver.start();
	}
	/**
	 * Filter all mazes with the filter f
	 * @param f
	 */
	private void filterSelectedMazes() {
		for (int i = 0; i < mad.getDisplayCount(); i++)
			if (mad.isSelected(i))
				filterMazeAt(i,currentFilter);
	}
	/**
	 * Applies the filter f with the current configuration to the i<i>th</i> maze.
	 * @param i
	 * @param f
	 */
	private void filterMazeAt(int i, Filters f) {
		MazeTraveller mazeFilter = Filters.getAssociatedFilter(f, ma.getElementAt(i));
		mazeFilter.setConfig(currentFilterConfig);

		mad.setTravellerForDisplay(mazeFilter, i);
		mazeFilter.start();
	}
	//////
	
	JMenuBar topMenuBar = new JMenuBar() {
		private static final long serialVersionUID = 1L;

		{
			
			//CONTROLES DE ACCIONES
			JMenu actionControls = new JMenu("Acciones");
			
			JMenuItem okMenuItem = new JMenuItem("Aplicar cambios");
			okMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					setCurrentParamsToSelectedMazes();
				}
			});
			actionControls.add(okMenuItem);
			
			JMenuItem genMenuItem = new JMenuItem("Generar");
			genMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					generateSelectedMazes();
				}
			});
			actionControls.add(genMenuItem);
			
			JMenuItem solMenuItem = new JMenuItem("Resolver");
			solMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					solveSelectedMazes();
				}
			});
			actionControls.add(solMenuItem);
			actionControls.addSeparator();
			
			JMenuItem allMenuItem = new JMenuItem("Marcar todo");
			allMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					mad.setSelectionOfAll(true);
				}
			});
			actionControls.add(allMenuItem);
			
			JMenuItem noneMenuItem = new JMenuItem("Desmarcar todo");
			noneMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					mad.setSelectionOfAll(false);
				}
			});
			actionControls.add(noneMenuItem);
			
			JMenuItem invMenuItem = new JMenuItem("Invertir selección");
			invMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					mad.invertSelectionOfAll();
				}
			});
			actionControls.add(invMenuItem);
			
			this.add(actionControls);
			////////////
			
			
			//AJUSTES
			JMenu settings = new JMenu("Ajustes");
			
			final JCheckBoxMenuItem tDataMenuItem = new JCheckBoxMenuItem("Mostrar datos T");
			tDataMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					setTData(tDataMenuItem.isSelected());
				}
			});
			settings.add(tDataMenuItem);
			
			final JCheckBoxMenuItem mDataMenuItem = new JCheckBoxMenuItem("Mostrar datos M");
			mDataMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					setMData(mDataMenuItem.isSelected());
				}
			});
			settings.add(mDataMenuItem);
			settings.addSeparator();
			
			final JCheckBoxMenuItem renderOption = new JCheckBoxMenuItem("Thin Render");
			renderOption.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					setThinRender(renderOption.isSelected());
				}
			});
			settings.add(renderOption);
			
			final JCheckBoxMenuItem renderAdv = new JCheckBoxMenuItem("Advanced Render");
			renderAdv.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					setAdvRender(renderAdv.isSelected());
				}
			});
			settings.add(renderAdv);
			settings.addSeparator();
			
			JMenu subMenuGen = new JMenu("Generador");
			ButtonGroup genGroup = new ButtonGroup();
			for (Integer i = 0; i < Generators.values().length; i++) {
				final JRadioButtonMenuItem iGen = new JRadioButtonMenuItem(Generators.values()[i].toString());
				iGen.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						Generators g = Generators.valueOf(iGen.getText());
						setCurrentGenerator(g);
					}
				});
				subMenuGen.add(iGen);
				genGroup.add(iGen);
			}
			settings.add(subMenuGen);
			
			JMenu subMenuSol = new JMenu("Resolutor");
			ButtonGroup solGroup = new ButtonGroup();
			for (Integer i = 0; i < Solvers.values().length; i++) {
				final JRadioButtonMenuItem iGen = new JRadioButtonMenuItem(Solvers.values()[i].toString());
				iGen.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						Solvers g = Solvers.valueOf(iGen.getText());
						setCurrentSolver(g);
					}
				});
				subMenuSol.add(iGen);
				solGroup.add(iGen);
			}
			settings.add(subMenuSol);
			this.add(settings);
			////////////////////////////
			
			//////FILTROS
			JMenu filterMenu = new JMenu("Filtros");
			
			JMenu subMenuReset = new JMenu("Reset");
			JMenuItem fillFilter = new JMenuItem("Fill");
			fillFilter.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					fillAllSelectedMazes(true);
				}
			});
			subMenuReset.add(fillFilter);
			
			JMenuItem clearFilter = new JMenuItem("Clear");
			clearFilter.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					fillAllSelectedMazes(false);
				}
			});
			subMenuReset.add(clearFilter);
			
			JMenuItem rstColorFilter = new JMenuItem("Reset Color");
			rstColorFilter.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					resetSelectedMazesColor();
				}
			});
			subMenuReset.add(rstColorFilter);
			filterMenu.add(subMenuReset);
			
			for (Integer i = 0; i < Filters.values().length; i++) {
				final JMenuItem iFil = new JMenuItem(Filters.values()[i].toString());
				iFil.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						setCurrentFilter(Filters.valueOf(iFil.getText()));
						filterSelectedMazes();
					}
				});
				filterMenu.add(iFil);
			}
			
			this.add(filterMenu);
			/////////////////
			
			
			///////AYUDA
			JMenu helpMenu = new JMenu("Ayuda");
			JMenuItem genHelp = new JMenuItem("Generador");
			genHelp.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JOptionPane.showMessageDialog(window, Generators.getHelp(currentGenerator), currentGenerator.toString(), JOptionPane.INFORMATION_MESSAGE);
				}
			});
			helpMenu.add(genHelp);
			JMenuItem solHelp = new JMenuItem("Resolutor");
			solHelp.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JOptionPane.showMessageDialog(window, Solvers.getHelp(currentSolver), currentSolver.toString(), JOptionPane.INFORMATION_MESSAGE);
				}
			});
			helpMenu.add(solHelp);
			JMenuItem filHelp = new JMenuItem("Filtro");
			filHelp.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JOptionPane.showMessageDialog(window, Filters.getHelp(currentFilter), currentFilter.toString(), JOptionPane.INFORMATION_MESSAGE);
				}
			});
			helpMenu.add(filHelp);
			this.add(helpMenu);
			
			//////////////////////////
			
			this.add(new JSeparator(JSeparator.VERTICAL));
			
			
			//////////////////////OTROS AJUSTES			
			this.add(new JLabel("Laberintos:"));
			this.add(new JSpinner(new SpinnerNumberModel(1,1,256,1)) {
				/**
				 * 
				 */
				private JSpinner sp = this;
				private static final long serialVersionUID = -310227146274846471L;
				{
					this.addChangeListener(new ChangeListener() {
						@Override
						public void stateChanged(ChangeEvent arg0) {
							restartViews((int)sp.getValue());
						}
					});
					this.setMaximumSize(new Dimension(40,40));
				}
			});
			
			this.add(new JLabel("     "));
			this.add(new JLabel("W:"));
			this.add(new JSpinner(new SpinnerNumberModel(width,1,2047,2)) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 5676603605693366300L;
				private JSpinner sp = this;
				{
					this.addChangeListener(new ChangeListener() {
						@Override
						public void stateChanged(ChangeEvent arg0) {
							int value = (int)sp.getValue();
							if (value%2 == 0)
								sp.setValue(value+1);
							setCurrentWidth((int)sp.getValue());
						}
					});
					this.setMaximumSize(new Dimension(50,40));
				}
			});

			this.add(new JLabel("H:"));
			this.add(new JSpinner(new SpinnerNumberModel(height,1,2047,2)) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 4734715151980774185L;
				private JSpinner sp = this;
				{
					this.addChangeListener(new ChangeListener() {
						@Override
						public void stateChanged(ChangeEvent arg0) {
							int value = (int)sp.getValue();
							if (value%2 == 0)
								sp.setValue(value+1);
							setCurrentHeight((int)sp.getValue());
						}
					});
					this.setMaximumSize(new Dimension(50,40));
				}
			});
			/////////////////////////////
			
		
			this.add(new JLabel("     "));			
			
			
			///////CONTROLES DE REPRODUCCIÓN
			this.add(new JButton("►") {
				/**
				 * 
				 */
				private static final long serialVersionUID = 8991357315740769716L;
				{
					this.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent arg0) {
							playSelected();
						}
					});
				}
			});
			this.add(new JButton("■") {
				/**
				 * 
				 */
				private static final long serialVersionUID = 8991357315740769716L;
				{
					this.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent arg0) {
							stopSelected();
						}
					});
				}
			});
			this.add(new JButton("↻") {
				/**
				 * 
				 */
				private static final long serialVersionUID = 8991357315740769716L;
				{
					this.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent arg0) {
							stepSelected();
						}
					});
				}
			});
			//////////////////////////////
		}
	};
	
	
	JMenuBar bottomMenuBar = new JMenuBar() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 6097300568711790777L;

		{
			this.add(new JLabel("GenCfg"));
			this.add(new UTJTextField() {	
				/**
				 * 
				 */
				private static final long serialVersionUID = -4263135285742377184L;
				{
					this.setText(currentGeneratorConfig);
				}
				@Override
				protected void onTextUpdate(String text) {
					setCurrentGeneratorConfig(text);
				}
			});
			
			this.add(new JLabel("SolCfg"));
			this.add(new UTJTextField() {	
				/**
				 * 
				 */
				private static final long serialVersionUID = 758019082595497390L;
				{
					this.setText(currentSolverConfig);
				}
				@Override
				protected void onTextUpdate(String text) {
					setCurrentSolverConfig(text);
				}
			});
			
			this.add(new JLabel("FilCfg"));
			this.add(new UTJTextField() {	
				/**
				 * 
				 */
				private static final long serialVersionUID = 758019082595497390L;
				{
					this.setText(currentFilterConfig);
				}
				@Override
				protected void onTextUpdate(String text) {
					setCurrentFilterConfig(text);
				}
			});
			
		}
	};

}


//menu = new JMenu("Help");
//menu.setMnemonic(KeyEvent.VK_P);
//menu.getAccessibleContext().setAccessibleDescription(
//        "The only menu in this program that has menu items");
////this.add(menu);
//
////a group of JMenuItems
//menuItem = new JMenuItem("A text-only menu item", KeyEvent.VK_T);
//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
//menuItem.getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
//menu.add(menuItem);
//
//menuItem = new JMenuItem("Both text and icon", new ImageIcon("images/middle.gif"));
//menuItem.setMnemonic(KeyEvent.VK_B);
//menu.add(menuItem);
//
//menuItem = new JMenuItem(new ImageIcon("images/middle.gif"));
//menuItem.setMnemonic(KeyEvent.VK_D);
//menu.add(menuItem);
//
////a group of radio button menu items
//menu.addSeparator();
//ButtonGroup group = new ButtonGroup();
//rbMenuItem = new JRadioButtonMenuItem("A radio button menu item");
//rbMenuItem.setSelected(true);
//rbMenuItem.setMnemonic(KeyEvent.VK_R);
//group.add(rbMenuItem);
//menu.add(rbMenuItem);
//
//rbMenuItem = new JRadioButtonMenuItem("Another one");
//rbMenuItem.setMnemonic(KeyEvent.VK_O);
//group.add(rbMenuItem);
//menu.add(rbMenuItem);
//
////a group of check box menu items
//menu.addSeparator();
//cbMenuItem = new JCheckBoxMenuItem("A check box menu item");
//cbMenuItem.setMnemonic(KeyEvent.VK_C);
//menu.add(cbMenuItem);
//
//cbMenuItem = new JCheckBoxMenuItem("Another one");
//cbMenuItem.setMnemonic(KeyEvent.VK_H);
//menu.add(cbMenuItem);
//
////a submenu
//menu.addSeparator();
//submenu = new JMenu("A submenu");
//submenu.setMnemonic(KeyEvent.VK_S);
//
//menuItem = new JMenuItem("An item in the submenu");
//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, ActionEvent.ALT_MASK));
//submenu.add(menuItem);
//
//menuItem = new JMenuItem("Another item");
//submenu.add(menuItem);
//menu.add(submenu);
//
////Build second menu in the menu bar.
//menu = new JMenu("File");
//menu.setMnemonic(KeyEvent.VK_F);
//menu.getAccessibleContext().setAccessibleDescription("This menu does nothing");
//this.add(menu);
//
////Build third menu in the menu bar
//menuItem = new JMenuItem("Quit");
//menuItem.addActionListener(new ActionListener() {
//	@Override
//	public void actionPerformed(ActionEvent arg0) {
//		if (JOptionPane.showConfirmDialog(getTopLevelAncestor(), "¿De verdad deseas salir?", "WALL·E says", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
//			c.requestQuit();
//	}
//});
//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.ALT_MASK));
//menuItem.getAccessibleContext().setAccessibleDescription("Help");
//menu.add(menuItem);
