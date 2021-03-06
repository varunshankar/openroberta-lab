// This file is automatically generated by the Open Roberta Lab.

#include <math.h>
#include <DHT.h>
#include <RobertaFunctions.h>   // Open Roberta library
#include <NEPODefs.h>

RobertaFunctions rob;

double item;
double item2;
#define DHTPINH 2
#define DHTTYPE DHT11
DHT _dht_H(DHTPINH, DHTTYPE);
void setup()
{
    Serial.begin(9600); 
    _dht_H.begin();
    item = _dht_H.readHumidity();
    item2 = _dht_H.readTemperature();
}

void loop()
{
}