output_file = "GeneratedTasks.kt"

kotlin_code_template = """
object Task%s: SimpleTasks {
    private val currentDate = Calendar.getInstance()

    private val androidTasks = AndroidTasks(
        Random(10).nextInt(),
        Random(10).nextInt(),
        Random(10).nextInt(),
    )
    
    override fun showDate(): String {
        currentDate.add(Calendar.DAY_OF_YEAR, %s)
        val time = currentDate.time
        val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd")
        return simpleDateFormat.format(time)
    }

}
"""

with open(output_file, "w") as file:
    for count in range(1, 201):
        file.write(kotlin_code_template % (count, count))

print(f"{output_file} generated successfully with 200 objects.")
