# Vision

There are two main goals for vision in 2019. The first is teleoperability in sandstorm.
This will not be difficult as FIRST has shipped us working code to achieve this. The second is for the robot to be able to see vision targets on the field and react to them automatically, which could be useful both during teleop and for any autonomous code we write. This doc discusses the second goal.

## Retroreflection
The vision targets on the field are [retroreflectors](https://en.wikipedia.org/wiki/Retroreflector). They have the same properties as the reflectors on bicycles and the eyes of cats: they reflect light back where it came from. On a bicycle, for example, the rear reflector reflects light from the headlight of a car back towards the car, meaning that to the driver the reflector appears to glow very brightly. We can use the same effect for finding the vision targets.

The approach WildStang used in 2017 is to have bright LEDS in a circle around the camera lighting up the target. Because the light is coming from so close to the camera, the retroreflecive vision targets send that light right back to the camera, making the vision targets appear to be very bright relative to everything else. This is a well-worn approach and probably the one we will take this year.

## Difficulties in 2017
### Ambient light
In 2017 WildStang had issues with the camera being confused by ambient light. In particular, the camera used had automatic exposure control. If the camera saw the light fixtures in the room, it would wash out and try to darken its image to compensate, which would unfortunately ruin its picture of the vision targets.

### Camera location
The camera was mounted low on the robot. Not only did this exacerbate the issue of being washed out by ceiling lights, as the camera was pointing somewhat upwards, it also caused the image of the target to be cut off when the robot was close to the target.

### Latency
For reasons never fully understood, the 2017 vision system had a few hundred milliseconds of latency --- which is to say, a few tenths of a second. This does not sound like very much, but unfortunately it is too much to make controlling the robot from the vision stream practical.

## Potential solutions in 2019
### Color filters
The LEDs we put around the camera can be any color we like. If we choose for them to be monochromatic, emitting only one wavelength of light, then we can put a color filter on the camera that only accepts light in that same wavelength. This will dramatically reduce the amount of ambient light that we can be confused by.

https://www.edmundoptics.com/resources/application-notes/imaging/filtering-in-machine-vision/

https://www.edmundoptics.com/c/bandpass-filters/617/

https://smartvisionlights.com/products/filters

We could also put a filter on the camera through which the operators are looking. That filter could block the LED light, so that the bright vision light doesn't make it hard for them to see what they are doing.

### Camera choice
The worst thing the camera can do is adjust the picture while we're trying to use it and screw up our view. We should use a camera whose exposure and focuse settings we can control, so that we can find settings that work and keep them; or use a camera with fixed settings that can't change.

### Really bright ring light
The brighter the light we put around the camera, the more effective it will be, as long as we don't run afoul of rule R9:
"High intensity light sources used on the ROBOT (e.g. super bright LED sources
marketed as 'military grade' or 'self-defense') may only be illuminated for a brief time
while targeting and may need to be shrouded to prevent any exposure to
participants. Complaints about the use of such light sources will be followed by reinspection and possible disablement of the device."

### Camera location
The best position for the camera may be level with or even above the target, so that the camera can only see things that might be relevant. Any visibility over the target or more than a certain distance ahead is not helpful and might allow the camera to see things that confuse the vision system.

