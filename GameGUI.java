import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.control.TextField; 
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import java.util.Scanner;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Timer;
import java.util.TimerTask;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.WindowEvent;
import javafx.scene.paint.Color;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.media.*;
import java.io.PrintWriter;
import java.util.concurrent.Executors;
import java.lang.InterruptedException;
import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class GameGUI extends Application{
	final String[] words = new String[200];
	final String[] wordsToPickFrom = new String[1000];
	final Random rand = new Random();
	String legalInput = "[a-z]";
	final Pattern regex = Pattern.compile(legalInput);
	Matcher match;
	Scanner sc;
	int index;
	int score;
	String ord = null;
	double secondsLeft;
	String input;
	Text nextword;
	Button btn;
	Button start;
	DecimalFormat number = new DecimalFormat("#.0");
	int wordIndex = 0;
	int highScore = 0;
	PrintWriter pw = null;
	Text beste = new Text();

	public String getWord(int i){
		return words[i]; 
	}

	public void fillArray(){

		for(int i = 0; i < words.length; i++){
			words[i] = wordsToPickFrom[rand.nextInt(1000)]; 

 		}
	}

	public void highLightLetters(String currentWord, int i, String letter){
		if(currentWord.length() <= i || i < 0){
			//Do nothing
		}
		else if(currentWord.charAt(i) == letter.charAt(0)){
			wordIndex++;
		}else{
			wordIndex++;
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception{
		ExecutorService executor = Executors.newFixedThreadPool(1);
		Text poeng = new Text("Score: 0");
		Text word = new Text("Word to type");
		Text time = new Text("Time left");
		TextField text = new TextField();

	Runnable task = () -> {

		score = 0;
		secondsLeft = 20;
		text.setVisible(true);
		text.setText("");
		btn.setVisible(false);
		poeng.setText("Score: " + Integer.toString(score));
		fillArray();
		wordIndex = 0;
		index = 0;
		ord = getWord(index++);
		nextword.setOpacity(0.5);
		word.setText(ord);
		nextword.setText(getWord(index));
		
		while(secondsLeft >= 0.00005){
		 	try{
				TimeUnit.MILLISECONDS.sleep(100);
			}catch(InterruptedException exe){
				executor.shutdownNow();
				break;
			}

			secondsLeft -= 0.1;
			time.setText(number.format(secondsLeft));
		 }
		 text.setVisible(false);
		 btn.setVisible(true);
		 time.setText("0.0");
		 if(score > highScore){
		 	highScore = score;
		 	beste.setText("Highscore: " + Integer.toString(highScore));
		 }
	};
		beste.setText("Highscore: 0");
		btn = new Button();
		start = new Button();
		start.setText("Start");
		btn.setText("Restart");
		nextword = new Text("Neste ordet");

		try{
			sc = new Scanner(new File("ord.txt"));
			for(int i = 0; i < wordsToPickFrom.length; i++){
				wordsToPickFrom[i] = sc.nextLine();
			}
		}catch(FileNotFoundException exe){
			System.out.println(exe.getMessage());
		}

		try{
			sc = new Scanner(new File("hs.txt"));
			highScore = Integer.parseInt(sc.nextLine());
			beste.setText("Highscore: " + Integer.toString(highScore));
			pw = new PrintWriter("hs.txt", "UTF-8");
		}catch(FileNotFoundException exe){
			pw = new PrintWriter("hs.txt", "UTF-8");
		}

		GridPane gridPane = new GridPane();
		gridPane.setMinSize(400,200);
		gridPane.setPadding(new Insets(10,10,10,10));
		gridPane.setVgap(3);
		gridPane.setHgap(3);
		gridPane.setAlignment(Pos.CENTER);

		GridPane gridPane1 = new GridPane();
		gridPane1.setMinSize(400,200);
		gridPane1.setPadding(new Insets(10,10,10,10));
		gridPane1.setVgap(5);
		gridPane1.setHgap(5);
		gridPane1.setAlignment(Pos.CENTER);

		Scene startScene = new Scene(gridPane1);
		text.setAlignment(Pos.CENTER);
		Scene scene = new Scene(gridPane);

		start.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event){
				primaryStage.setScene(scene);
				executor.execute(task);
				primaryStage.show();
			}
		});

		text.setOnKeyPressed(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event){
				//User done writing the word
				if(event.getCode() == KeyCode.SPACE){
						input = text.getCharacters().toString();
						if(ord.equals(input.trim())){
							poeng.setText("Score: " + Integer.toString(++score));
						}
						ord = getWord(index++);
						word.setText(ord);
						nextword.setText(getWord(index));
						wordIndex = 0;
						text.clear();
					}else{
						//Erases characters, update the wordindex accordingly
						if(event.getCode() == KeyCode.BACK_SPACE){
							if(wordIndex > 0){
								wordIndex--;
							}
						}else{
							//Checking that the letter is of legal input only letters from a-z
							match = regex.matcher(event.getText());
							if(match.find()){
								highLightLetters(ord, wordIndex, event.getText());
							}
						}
					}
				}
			
		});
		btn.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent event){
				executor.execute(task);
			}
		});

		gridPane1.add(start, 0, 0);
		gridPane.add(text, 0, 2);
		gridPane.add(word, 0, 0);
		gridPane.add(nextword, 0, 1);
		gridPane.add(poeng, 2, 0);
		gridPane.add(beste, 3, 0);
		gridPane.add(time, 0, 3);
		gridPane.add(btn, 0, 2);

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we){
				if(score > highScore){
					highScore = score;
				}
				pw.write(Integer.toString(highScore));
				pw.close();
				executor.shutdownNow();
				sc.close();
				primaryStage.close();
			}
		});
		primaryStage.setScene(startScene);

		primaryStage.show();
	}

}