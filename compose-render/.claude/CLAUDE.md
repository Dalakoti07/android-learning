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

# Implement
Example 1 — Audio Visualizer → proves multiple measure passes

Structure in XML:
A row of bars inside a LinearLayout (horizontal, layout_weight on each bar to split width equally), that LinearLayout inside a RelativeLayout or ConstraintLayout.

Why it proves the point:
- LinearLayout with layout_weight always does 2 measure passes — first pass to measure unweighted children, second pass to distribute the remaining space among weighted ones
- If you nest that inside a RelativeLayout, RelativeLayout itself also does 2 internal passes (one horizontal, one vertical)
- Each bar's onMeasure gets called 4 times (2 × 2) before a single pixel is drawn
- In Compose, the same bars in a custom Layout {} are each measured exactly once
- The log difference is stark and countable

---

Example 2 — Speedometer → proves scoped recomposition

Structure:
Split into clearly separate parts — outer dial with tick marks and labels (never changes), the needle (rotates as speed changes), the current speed number (updates as speed
changes), unit label "km/h" (never changes).

Why it proves the point:

In XML: the needle's angle changes on a timer. Whether you use a single canvas View calling invalidate() or separate views in a ConstraintLayout, updating the needle causes the
parent to invalidate/relayout — the static dial markings and unit label redraw on every single frame even though their pixels are identical.

In Compose: each part is a separate composable reading only the state it needs:
- DialBackground() reads no state → logs Recomposition exactly once at startup, then never again
- SpeedNeedle(angle) reads only angle state → recomposes on every tick
- SpeedText(speed) reads only speed state → recomposes when speed changes
- UnitLabel() reads no state → logs Recomposition exactly once, then never again

The logs make it undeniable: XML redraws the entire speedometer 60 times per second. Compose only recomposes the needle and number, leaving the static parts completely untouched.
