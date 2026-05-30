In this repository we are comparing the rendering framework in android world

Which is basically
- XML views
- Compose

Now we have already created 1 successful example where we have implemented circular progress bar in XML and compose (Read project)
and we have placed both of these implementation in 1 fragment and put that fragment in mainActivity

Now we want to do some more complex examples to go into depth of both of these rendering frameworks, where compose advocates itself 
that it only does 1 pass nothing more

Given that UI appearance should be same for both XML views and compose

So all examples that we should be trying are following (both XML and compose)
- Custom chip layout
  - means initially all chips flow horizontally and if not sufficient space then go to next line
  - now we can change the orientation of chips arrange and on button click we can do that only 2 chips per row thats it
  - child layout logic
  - child change can trigger re render in some sense
- Audio Visualizer
  - ```txt
        |
        |||
        ||||||
        |||||||||
        |||||
        ||
   ```
  - draw rect
  - invalidate
  - animations
- Speedometer
- Pie chart
- battery view
- Line chart
- Analog clock
- waveform view
- compass
- mini google maps marker cluster
- 