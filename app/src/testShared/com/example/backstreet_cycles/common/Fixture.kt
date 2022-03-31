package com.example.backstreet_cycles.common

import com.mapbox.api.directions.v5.models.DirectionsRoute

object Fixture {

    val journeyOverview = DirectionsRoute.fromJson("""routeIndex=0, distance=3420.9, duration=1391.1, durationTypical=null, geometry=csagaBjx_FcA} GeBiL_BsL`DqBlAo@nBw@~GeDX{AlV}KvD_@~Au@mCsT]iC{AoRoBuJSo@iCcD`CeGxBiD`DmB|KqG|BwDLiRSoFkA{OkJqGyA_Au@HgCtBcIxGaFvDyDvCaFs^uBaRfT_JsAkMiAsG_Gq^[mAwCcPmCaHcC}]@cYrD?dC??}@AkN@sCAeA^qC?cThAKpLAnA@dVJha@DxHD^?|@FM{@GwBd@yPVaHTaH\cKFoBFmH@ur@BstARae@F}b@J}h@g@_Q[_LQmFi@gOsAyUy@eAu@_@aAbAiCaAcDq@_Ay@SeEh@uk@jAcjAtAufCHoI?qGJwO`@iXx@kVLaDx@}UhAmMjAwOzB_[y@KeB]^uH?cSMyB_@{AwAeArAg_@B}@JcC`AaVhEs`@TgI?sAIoNFeGR}CZ{B`@mB`Jmb@jCv@ZJrE|A^Ln@Tf@T\}Ch@wFjFim@t@yEnEi]V_B^wB`@_CdFgXpCoNdBuJiAa@i@Qg@SiNuGwJuEcA_@kL}Fa@Sw@_@y@c@oIyF}HiEbBgOlAqKfA{NC]IuCXiCj@{ElAeH`@wBt@sDl@{DNmFAwFI}ER}EVsDbAeIzAmKxAoN|@yECqGUePAaBHoK\oI\mFNgBd@eHt@qNnAsa@ZiH^eGx@iFzB}IlCoKxRgs@pFuRnDiMlFoRLgDpCqIhAeEd@mBp@oCViAfAaAj@oDkBkHo@oDe@uG]cLLuQd@eYXkKJoG`@_ITuDdBoNZiCd@cE\wIkBst@WaFU{DtB?nD@Y}McA}XS}Hi@uOA]_@kI, weight=1437.7, weightName=cyclability, legs=[RouteLeg{distance=3420.9, duration=1391.1, durationTypical=null, summary=Upper Thames Street, Castle Baynard Street, admins=null, steps=[LegStep{distance=42.4, duration=42.4, durationTypical=null, speedLimitUnit=null, speedLimitSign=null, geometry=csagaBjx_FcA}GeBiL_BsL, name=Strand, ref=null, destinations=null, mode=pushing bike, pronunciation=null, rotaryName=null, rotaryPronunciation=null, maneuver=StepManeuver{rawLocation=[-0.115094, 51.512642], bearingBefore=0.0, bearingAfter=69.0, instruction=Head northeast on Strand, type=depart, modifier=right, exit=null}, voiceInstructions=[VoiceInstructions{distanceAlongGeometry=42.4, announcement=Head northeast on Strand, then turn right onto Arundel Street, ssmlAnnouncement=<speak><amazon:effect name="drc"><prosody rate="1.08">Head northeast on Strand, then turn right onto Arundel Street</prosody></amazon:effect></speak>}, VoiceInstructions{distanceAlongGeometry=15.0, announcement=Turn right onto Arundel Street, then turn left to stay on Arundel Street, ssmlAnnouncement=<speak><amazon:effect name="drc"><prosody rate="1.08">Turn right onto Arundel Street, then turn left to stay on Arundel Street</prosody></amazon:effect></speak>}], bannerInstructions=[BannerInstructions{distanceAlongGeometry=42.4, primary=BannerText{text=Arundel Street, components=[BannerComponents{text=Arundel Street, type=text, subType=null, abbreviation=Arundel St, abbreviationPriority=0, imageBaseUrl=null, mapboxShield=null, imageUrl=null, directions=null, active=null, activeDirection=null}], type=turn, modifier=right, degrees=null, drivingSide=null}, secondary=null, sub=null, view=null}, BannerInstructions{distanceAlongGeometry=15.0, primary=BannerText{text=Arundel Street, components=[BannerComponents{text=Arundel Street, type=text, subType=null, abbreviation=Arundel St, abbreviationPriority=0, imageBaseUrl=null, mapboxShield=null, imageUrl=null, directions=null, active=null, activeDirection=null}], type=turn, modifier=right, degrees=null, drivingSide=null}, secondary=null, sub=BannerText{text=Arundel Street, components=[BannerComponents{text=Arundel Street, type=text, subType=null, abbreviation=Arundel St, abbreviationPriority=0, imageBaseUrl=null, mapboxShield=null, imageUrl=null, directions=null, active=null, activeDirection=null}], type=turn, modifier=left, degrees=null, drivingSide=null}, view=null}], drivingSide=right, weight=42.4, intersections=[StepIntersection{rawLocation=[-0.115094, 51.512642], bearings=[69], classes=null, entry=[true], in=null, out=0, lanes=null, geometryIndex=null, isUrban=null, adminIndex=null, restStop=null, tollCollection=null, mapboxStreetsV8=null, tunnelName=null}], exits=null}, LegStep{distance=38.0, duration=30.4, durationTypical=null, speedLimitUnit=null, speedLimitSign=null, geometry=m{agaBnt~E`DqBlAo@nBw@~GeD, name=Arundel Street, ref=null, destinations=null, mode=cycling, pronunciation=null, rotaryName=null, rotaryPronunciation=null, maneuver=StepManeuver{rawLocation=[-0.11452, 51.512775], bearingBefore=68.0, bearingAfter=158.0, instruction=Turn right onto Arundel Street, type=turn, modifier=right, ex""")

    val startWalk = DirectionsRoute.fromJson("""routeIndex=0, distance=414.318, duration=286.35, durationTypical=null, geometry=yo_gaBj~`F`b@sWfCqBqC}Rq@uF_Le~@q@uF_TbJeSdJ_Br@yD^mV~KYzA_HbDoBx@mAn@_DpB|ArLdBhLbA|G, weight=938.907, weightName=pedestrian, legs=[RouteLeg{distance=414.318, duration=286.35, durationTypical=null, summary=Temple Place, Arundel Street, admins=[Admin{countryCode=GB, countryCodeAlpha3=GBR}], steps=[LegStep{distance=76.704, duration=38.352, durationTypical=null, speedLimitUnit=null, speedLimitSign=null, geometry=yo_gaBj~`F`b@sWfCqB, name=Strand Lane, ref=null, destinations=null, mode=walking, pronunciation=null, rotaryName=null, rotaryPronunciation=null, maneuver=StepManeuver{rawLocation=[-0.115702, 51.511565], bearingBefore=0.0, bearingAfter=156.0, instruction=Walk southeast on Strand Lane., type=depart, modifier=null, exit=null}, voiceInstructions=[VoiceInstructions{distanceAlongGeometry=76.704, announcement=Walk southeast on Strand Lane., ssmlAnnouncement=<speak><amazon:effect name="drc"><prosody rate="1.08">Walk southeast on <say-as interpret-as="address">Strand Lane</say-as>.</prosody></amazon:effect></speak>}, VoiceInstructions{distanceAlongGeometry=75.004, announcement=In 300 feet, Turn left onto Temple Place., ssmlAnnouncement=<speak><amazon:effect name="drc"><prosody rate="1.08">In 300 feet, Turn left onto <say-as interpret-as="address">Temple Place</say-as>.</prosody></amazon:effect></speak>}, VoiceInstructions{distanceAlongGeometry=8.5, announcement=Turn left onto Temple Place., ssmlAnnouncement=<speak><amazon:effect name="drc"><prosody rate="1.08">Turn left onto <say-as interpret-as="address">Temple Place</say-as>.</prosody></amazon:effect></speak>}], bannerInstructions=[BannerInstructions{distanceAlongGeometry=76.704, primary=BannerText{text=Temple Place, components=[BannerComponents{text=Temple Place, type=text, subType=null, abbreviation=null, abbreviationPriority=null, imageBaseUrl=null, mapboxShield=null, imageUrl=null, directions=null, active=null, activeDirection=null}], type=turn, modifier=left, degrees=null, drivingSide=null}, secondary=null, sub=null, view=null}], drivingSide=left, weight=57.528, intersections=[StepIntersection{rawLocation=[-0.115702, 51.511565], bearings=[156], classes=null, entry=[true], in=null, out=0, lanes=null, geometryIndex=0, isUrban=true, adminIndex=0, restStop=null, tollCollection=null, mapboxStreetsV8=MapboxStreetsV8{roadClass=service}, tunnelName=null}], exits=null}, LegStep{distance=116.0, duration=83.127, durationTypical=null, speedLimitUnit=null, speedLimitSign=null, geometry=oh~faBdb`FqC}Rq@uF_Le~@q@uF, name=Temple Place, ref=null, destinations=null, mode=walking, pronunciation=null, rotaryName=null, rotaryPronunciation=null, maneuver=StepManeuver{rawLocation=[-0.115251, 51.510936], bearingBefore=152.0, bearingAfter=70.0, instruction=Turn left onto Temple Place., type=turn, modifier=left, exit=null}, voiceInstructions=[VoiceInstructions{distanceAlongGeometry=114.3, announcement=Continue for 400 feet., ssmlAnnouncement=<speak><amazon:effect name="drc"><prosody rate="1.08">Continue for 400 feet.</prosody></amazon:effect></speak>}, VoiceInstructions{distanceAlongGeometry=91.44, announcement=In 300 feet, Turn left onto Arundel Street., ssmlAnnouncement=<speak><amazon:effect name="drc"><prosody rate="1.08">In 300 feet, Turn left onto <say-as interpret-as="address">Arundel Street</say-as>.</prosody></amazon:effect></speak>}, VoiceInstructions{distanceAlongGeometry=8.5, announcement=Turn left onto Arundel Street., ssmlAnnouncement=<speak><amazon:effect name="drc"><prosody rate="1.08">Turn left onto <say-as interpret-as="address">Arundel Street</say-as>.</prosody></amazon:effect></speak>}], bannerInstructions=[BannerInstructions{distanceAlongGeometry=116.0, primary=BannerText{text=Arundel Street, components=[BannerComponents{text=Arundel Street, type=text, subType=null, abbreviation=null, abbreviationPriority=null, imageBaseUrl=null, mapboxShield=null, imageUrl=null, directions=null, active=null, activeDirection=null}], type=turn, modi""")

    val cycle = DirectionsRoute.fromJson("""routeIndex=0, distance=3420.9, duration=1391.1, durationTypical=null, geometry=csagaBjx_FcA} GeBiL_BsL`DqBlAo@nBw@~GeDX{AlV}KvD_@~Au@mCsT]iC{AoRoBuJSo@iCcD`CeGxBiD`DmB|KqG|BwDLiRSoFkA{OkJqGyA_Au@HgCtBcIxGaFvDyDvCaFs^uBaRfT_JsAkMiAsG_Gq^[mAwCcPmCaHcC}]@cYrD?dC??}@AkN@sCAeA^qC?cThAKpLAnA@dVJha@DxHD^?|@FM{@GwBd@yPVaHTaH\cKFoBFmH@ur@BstARae@F}b@J}h@g@_Q[_LQmFi@gOsAyUy@eAu@_@aAbAiCaAcDq@_Ay@SeEh@uk@jAcjAtAufCHoI?qGJwO`@iXx@kVLaDx@}UhAmMjAwOzB_[y@KeB]^uH?cSMyB_@{AwAeArAg_@B}@JcC`AaVhEs`@TgI?sAIoNFeGR}CZ{B`@mB`Jmb@jCv@ZJrE|A^Ln@Tf@T\}Ch@wFjFim@t@yEnEi]V_B^wB`@_CdFgXpCoNdBuJiAa@i@Qg@SiNuGwJuEcA_@kL}Fa@Sw@_@y@c@oIyF}HiEbBgOlAqKfA{NC]IuCXiCj@{ElAeH`@wBt@sDl@{DNmFAwFI}ER}EVsDbAeIzAmKxAoN|@yECqGUePAaBHoK\oI\mFNgBd@eHt@qNnAsa@ZiH^eGx@iFzB}IlCoKxRgs@pFuRnDiMlFoRLgDpCqIhAeEd@mBp@oCViAfAaAj@oDkBkHo@oDe@uG]cLLuQd@eYXkKJoG`@_ITuDdBoNZiCd@cE\wIkBst@WaFU{DtB?nD@Y}McA}XS}Hi@uOA]_@kI, weight=1437.7, weightName=cyclability, legs=[RouteLeg{distance=3420.9, duration=1391.1, durationTypical=null, summary=Upper Thames Street, Castle Baynard Street, admins=null, steps=[LegStep{distance=42.4, duration=42.4, durationTypical=null, speedLimitUnit=null, speedLimitSign=null, geometry=csagaBjx_FcA}GeBiL_BsL, name=Strand, ref=null, destinations=null, mode=pushing bike, pronunciation=null, rotaryName=null, rotaryPronunciation=null, maneuver=StepManeuver{rawLocation=[-0.115094, 51.512642], bearingBefore=0.0, bearingAfter=69.0, instruction=Head northeast on Strand, type=depart, modifier=right, exit=null}, voiceInstructions=[VoiceInstructions{distanceAlongGeometry=42.4, announcement=Head northeast on Strand, then turn right onto Arundel Street, ssmlAnnouncement=<speak><amazon:effect name="drc"><prosody rate="1.08">Head northeast on Strand, then turn right onto Arundel Street</prosody></amazon:effect></speak>}, VoiceInstructions{distanceAlongGeometry=15.0, announcement=Turn right onto Arundel Street, then turn left to stay on Arundel Street, ssmlAnnouncement=<speak><amazon:effect name="drc"><prosody rate="1.08">Turn right onto Arundel Street, then turn left to stay on Arundel Street</prosody></amazon:effect></speak>}], bannerInstructions=[BannerInstructions{distanceAlongGeometry=42.4, primary=BannerText{text=Arundel Street, components=[BannerComponents{text=Arundel Street, type=text, subType=null, abbreviation=Arundel St, abbreviationPriority=0, imageBaseUrl=null, mapboxShield=null, imageUrl=null, directions=null, active=null, activeDirection=null}], type=turn, modifier=right, degrees=null, drivingSide=null}, secondary=null, sub=null, view=null}, BannerInstructions{distanceAlongGeometry=15.0, primary=BannerText{text=Arundel Street, components=[BannerComponents{text=Arundel Street, type=text, subType=null, abbreviation=Arundel St, abbreviationPriority=0, imageBaseUrl=null, mapboxShield=null, imageUrl=null, directions=null, active=null, activeDirection=null}], type=turn, modifier=right, degrees=null, drivingSide=null}, secondary=null, sub=BannerText{text=Arundel Street, components=[BannerComponents{text=Arundel Street, type=text, subType=null, abbreviation=Arundel St, abbreviationPriority=0, imageBaseUrl=null, mapboxShield=null, imageUrl=null, directions=null, active=null, activeDirection=null}], type=turn, modifier=left, degrees=null, drivingSide=null}, view=null}], drivingSide=right, weight=42.4, intersections=[StepIntersection{rawLocation=[-0.115094, 51.512642], bearings=[69], classes=null, entry=[true], in=null, out=0, lanes=null, geometryIndex=null, isUrban=null, adminIndex=null, restStop=null, tollCollection=null, mapboxStreetsV8=null, tunnelName=null}], exits=null}, LegStep{distance=38.0, duration=30.4, durationTypical=null, speedLimitUnit=null, speedLimitSign=null, geometry=m{agaBnt~E`DqBlAo@nBw@~GeD, name=Arundel Street, ref=null, destinations=null, mode=cycling, pronunciation=null, rotaryName=null, rotaryPronunciation=null, maneuver=StepManeuver{rawLocation=[-0.11452, 51.512775], bearingBefore=68.0, bearingAfter=158.0, instruction=Turn right onto Arundel Street, type=turn, modifier=right, ex""")

    val endWalk = DirectionsRoute.fromJson("""routeIndex=0, distance=553.0, duration=399.338, durationTypical=null, geometry=eo{faBrirCcBga@?[?mD?aDXmNbB}HxN}QbBUvAPlK~ArABToDtCZdHRrNlBtL`Bta@zEdOzBvHv@hArBpC`@tD|@tBAtEnB~J`Gdf@vYbHnEjDpBtQrK, weight=498.899, weightName=pedestrian, legs=[RouteLeg{distance=553.0, duration=399.338, durationTypical=null, summary=A100, admins=[Admin{countryCode=GB, countryCodeAlpha3=GBR}], steps=[LegStep{distance=153.0, duration=119.479, durationTypical=null, speedLimitUnit=null, speedLimitSign=null, geometry=eo{faBrirCcBga@?[?mD?aDXmNbB}HxN}QbBUvAPlK~ArAB, name=, ref=null, destinations=null, mode=walking, pronunciation=null, rotaryName=null, rotaryPronunciation=null, maneuver=StepManeuver{rawLocation=[-0.075434, 51.509507], bearingBefore=0.0, bearingAfter=82.0, instruction=Walk east on the walkway., type=depart, modifier=null, exit=null}, voiceInstructions=[VoiceInstructions{distanceAlongGeometry=153.0, announcement=Walk east on the walkway for 500 feet., ssmlAnnouncement=<speak><amazon:effect name="drc"><prosody rate="1.08">Walk east on the walkway for 500 feet.</prosody></amazon:effect></speak>}, VoiceInstructions{distanceAlongGeometry=91.44, announcement=In 300 feet, Turn left onto the walkway., ssmlAnnouncement=<speak><amazon:effect name="drc"><prosody rate="1.08">In 300 feet, Turn left onto the walkway.</prosody></amazon:effect></speak>}, VoiceInstructions{distanceAlongGeometry=9.917, announcement=Turn left onto the walkway. Then Turn right onto A100., ssmlAnnouncement=<speak><amazon:effect name="drc"><prosody rate="1.08">Turn left onto the walkway. Then Turn right onto <say-as interpret-as="address">A100</say-as>.</prosody></amazon:effect></speak>}], bannerInstructions=[BannerInstructions{distanceAlongGeometry=153.0, primary=BannerText{text=Turn left onto the walkway, components=[BannerComponents{text=Turn left onto the walkway, type=text, subType=null, abbreviation=null, abbreviationPriority=null, imageBaseUrl=null, mapboxShield=null, imageUrl=null, directions=null, active=null, activeDirection=null}], type=turn, modifier=left, degrees=null, drivingSide=null}, secondary=null, sub=null, view=null}, BannerInstructions{distanceAlongGeometry=91.44, primary=BannerText{text=Turn left onto the walkway, components=[BannerComponents{text=Turn left onto the walkway, type=text, subType=null, abbreviation=null, abbreviationPriority=null, imageBaseUrl=null, mapboxShield=null, imageUrl=null, directions=null, active=null, activeDirection=null}], type=turn, modifier=left, degrees=null, drivingSide=null}, secondary=null, sub=BannerText{text=A100 / Tower Bridge Approach, components=[BannerComponents{text=A100, type=icon, subType=null, abbreviation=null, abbreviationPriority=null, imageBaseUrl=null, mapboxShield=MapboxShield{baseUrl=https://api.mapbox.com/styles/v1, name=rectangle-green, textColor=yellow, displayRef=A100}, imageUrl=null, directions=null, active=null, activeDirection=null}, BannerComponents{text=/, type=delimiter, subType=null, abbreviation=null, abbreviationPriority=null, imageBaseUrl=null, mapboxShield=null, imageUrl=null, directions=null, active=null, activeDirection=null}, BannerComponents{text=Tower Bridge Approach, type=text, subType=null, abbreviation=null, abbreviationPriority=null, imageBaseUrl=null, mapboxShield=null, imageUrl=null, directions=null, active=null, activeDirection=null}], type=turn, modifier=right, degrees=null, drivingSide=null}, view=null}], drivingSide=left, weight=172.086, intersections=[StepIntersection{rawLocation=[-0.075434, 51.509507], bearings=[82], classes=null, entry=[true], in=null, out=0, lanes=null, geometryIndex=0, isUrban=true, adminIndex=0, restStop=null, tollCollection=null, mapboxStreetsV8=MapboxStreetsV8{roadClass=service}, tunnelName=null}, StepIntersection{rawLocation=[-0.074872, 51.509557], bearings=[28, 90, 263], classes=null, entry=[true, true, false], in=2, out=1, lanes=null, geometryIndex=2, isUrban=true, adminIndex=0, restStop=null, tollCollection=null, mapboxStreetsV8=MapboxStreetsV8{roadClass=service}, tunnelName=nu""")

}