# Weather

A weather forecast app: people look up places and see current and upcoming weather for them.

## Places

**Location**:
A named place with a latitude and longitude, as returned by geocoding search.
_Avoid_: Place, city, spot, area

**Saved Location**:
A Location the user has chosen to keep in their list for quick access.
_Avoid_: Favourite, bookmark, pinned city

**Location Search**:
The screen where the user types a place name, sees matching Locations from Open-Meteo geocoding, and picks one to save as a Saved Location. Only geocoding results can be saved, never free text.
_Avoid_: City search, add city, place picker

**Current Location**:
The device's own position, resolved from GPS/network and reverse-labelled for display. Never stored in the Saved Location list.
_Avoid_: My location, GPS location, here

## Weather

**Current Conditions**:
The observed weather at a Location right now: temperature, feels-like, Weather Code, wind, humidity.
_Avoid_: Now, current weather, live weather

**Hourly Forecast**:
Predicted weather for a Location for each upcoming hour.
_Avoid_: Hourly weather, timeline

**Daily Forecast**:
Predicted weather for a Location for each upcoming day: min/max temperature, Weather Code, precipitation.
_Avoid_: Daily weather, week view, outlook

**Forecast**:
The combination of Current Conditions, Hourly Forecast and Daily Forecast for one Location at one fetch time.
_Avoid_: Weather data, report

**Weather Code**:
The WMO code describing the sky/precipitation state (e.g. 0 = clear, 61 = light rain), as returned by Open-Meteo.
_Avoid_: Condition, weather type, icon code

**Weather Condition**:
The app's own category for a Weather Code (Clear, Cloudy, Rain, Snow, Thunderstorm…), shown to users as a label and icon.
_Avoid_: Weather type, sky state

**Units**:
The user's chosen measurement system for temperature, wind speed and precipitation.
_Avoid_: Measurement settings, metric/imperial toggle
