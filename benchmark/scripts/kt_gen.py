output_file = "GeneratedTasks.kt"

kotlin_code_template = """
object Task%s: SimpleTasks {

    override fun showDate(): String {
        val currentDate = Calendar.getInstance()
        currentDate.add(Calendar.DAY_OF_YEAR, %s)
        val time = currentDate.time
        val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd")
        return simpleDateFormat.format(time)
    }

}
"""

with open(output_file, "w") as file:
    for count in range(1, 501):
        file.write(kotlin_code_template % (count, count))

print(f"{output_file} generated successfully with 200 objects.")