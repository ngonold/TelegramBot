import com.kotah.gamelogic.Task;
import com.kotah.parser.TaskFileParser;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestTaskParser {
    List<Task> taskList = new ArrayList<>();

    @Test
    public void testParser() {
        taskList = new TaskFileParser().readTasksFile("tasks.txt");
        System.out.println(taskList);
        Assert.assertNotNull(taskList);
    }
}
