int tempX, humidX, lightX, pressX, allX;  // Position of buttons X
int tempY, humidY, lightY, pressY, allY;  // Position of buttons Y
int buttonSizeX = 150; 
int buttonSizeY = 50;// Diameter of Button

boolean overTemp = false, overHumid = false, overLight = false, overPress = false, overAll = false;
color highlight = color(204);
color connectColor = color(0, 255, 0);
color notConnectColor = color(255, 0, 0);
color backgroundColor = color(255);
String temp = "";
String humid = "";
String light = "";
String press = "";

void setup() {
  size(545, 460);
  
  //set X Coordinate
  tempX = 50;
  humidX = 50;
  lightX = 50;
  pressX = 50;
  allX = 225;
  
  //set Y Coordinate
  tempY = 75;
  humidY = 150;
  lightY = 225;
  pressY = 300;
  allY = 375;
}

void draw() {
  update(mouseX, mouseY);
  background(backgroundColor);

  if (overTemp) {
    fill(highlight);
  } else {
    fill(color(255));
  }
  stroke(0);
  rect(tempX, tempY, buttonSizeX + 20, buttonSizeY);
  if (overHumid) {
    fill(highlight);
  } else {
    fill(color(255));
  }
  stroke(0);
  rect(humidX, humidY, buttonSizeX - 20, buttonSizeY);
  if (overLight) {
    fill(highlight);
  } else {
    fill(color(255));
  }
  stroke(0);
  rect(lightX, lightY, buttonSizeX - 20, buttonSizeY);
  if (overPress) {
    fill(highlight);
  } else {
    fill(color(255));
  }
  stroke(0);
  rect(pressX, pressY, buttonSizeX - 20, buttonSizeY);
  if (overAll) {
    fill(highlight);
  } else {
    fill(color(255));
  }
  stroke(0);
  rect(allX, allY, 100, 50);
  
  if (overAll) {
    fill(highlight);
  } else {
    fill(color(255));
  }
  
  textAlign(RIGHT);
  if (connected()) {
    fill(connectColor);
    textSize(24);
    text("Connected", 480, 50);
  } else {
    fill(notConnectColor);
    textSize(24);
    text("Not Connected", 480, 50);
  }
  textAlign(LEFT);
 
  // The Permanent Text is placed here
  fill(color(0));
  textSize(24);
  text("Weather Station", 25, 50);
  
  fill(color(0));
  textSize(24);
  text("Temperature", tempX + 10, tempY + 35);
  
  fill(color(0));
  textSize(24);
  text("Humidty", humidX + 20, humidY + 35);
  
  fill(color(0));
  textSize(24);
  text("Light", lightX + 30, lightY + 35);
  
  fill(color(0));
  textSize(24);
  text("Pressure", pressX + 15, pressY + 35);
  
  fill(color(0));
  textSize(24);
  text("Get All", allX + 10, allY + 35);
  
  // The Results are printed here
  fill(color(0));
  textSize(24);
  text(temp, tempX + 200, tempY + 35);
  
  fill(color(0));
  textSize(24);
  text(humid, humidX + 200, humidY + 35);
  
  fill(color(0));
  textSize(24);
  text(light, lightX + 200, lightY + 35);
  
  fill(color(0));
  textSize(24);
  text(press, pressX + 200, pressY + 35);
  
}

void update(int x, int y) {
  if (overButton(tempX, tempY, buttonSizeX, buttonSizeY)) {
    overTemp = true;
  } else {
    overTemp = false;
  }
  
  if (overButton(humidX, humidY, buttonSizeX, buttonSizeY)) {
    overHumid = true;
  } else {
    overHumid = false;
  }
  
  if (overButton(lightX, lightY, buttonSizeX, buttonSizeY)) {
    overLight = true;
  } else { 
    overLight = false;
  }
  
  if (overButton(pressX, pressY, buttonSizeX, buttonSizeY)) {
    overPress = true;
  } else {
    overPress = false;
  }
  
  if (overButton(allX, allY, 100, 50)) {
    overAll = true;
  } else {
    overAll = false;
  }
}

void mousePressed() {
  if (overTemp) {
    temp = "Temp °C";
  } else if (overHumid) {
    humid = "Humid RH";
  } else if (overLight) {
    light = "Daytime/Nighttime";
  } else if (overPress) {
    press = "Press Pa";
  } else if (overAll) {
    temp = "Temp °C";
    humid = "Humid RH";
    light = "Daytime/Nighttime";
    press = "Press Pa";
  }
}

boolean overButton(int x, int y, int width, int height)  {
  if (mouseX >= x && mouseX <= x+width && mouseY >= y && mouseY <= y+height) {
    return true;
  } else {
    return false;
  }
}

boolean connected() {
  return true;
}