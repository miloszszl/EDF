/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edf_projekt;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class EDFScheduler {

    static List<Task> schedule(final List<Task> taskList, int LCM) {

        List<Task> orderedTasks = new LinkedList<>();
        Map<Integer, List<Task>> waitingMap = new HashMap<>();

        for (int timeUnit = 0; timeUnit < LCM; timeUnit++) {
            
            for (Task t : taskList) {
                if (timeUnit % t.getPeriod() == 0) {

                    if (!waitingMap.containsKey(timeUnit + t.getPeriod())) {
                        waitingMap.put(timeUnit + t.getPeriod(), new LinkedList<>());
                    }

                    for (int i = 0; i < t.getExecutionTime(); i++) {
                        waitingMap.get(timeUnit + t.getPeriod()).add(t);
                    }
                }
            }

            if (!waitingMap.isEmpty()) {
                int minKey = Collections.min(waitingMap.keySet());
                orderedTasks.add(waitingMap.get(minKey).remove(0));
                if (waitingMap.get(minKey).isEmpty()) {
                    waitingMap.remove(minKey);
                }
            } else {
                orderedTasks.add(null);
            }
        }

        return orderedTasks;
    }

    static int getLeastCommonMultiple(List<Task> taskList) {
        int leastCommonMultiple = taskList.get(0).getPeriod();
        boolean LCMNotFound = true;
        while (LCMNotFound) {
            LCMNotFound = false;
            for (Task t : taskList) {
                if (leastCommonMultiple % t.getPeriod() != 0) {
                    LCMNotFound = true;
                    leastCommonMultiple++;
                    break;
                }
            }
        }
        return leastCommonMultiple;
    }

    static BigDecimal calculateUtilizationPercentage(final List<Task> taskList) {

        int LCM = getLeastCommonMultiple(taskList);
        int sum = calculateSumOfTime(taskList, LCM);

        BigDecimal U = new BigDecimal(sum * 100).divide(new BigDecimal(LCM), 2, RoundingMode.HALF_UP);
        return U;
    }

    static int calculateSumOfTime(final List<Task> taskList, int LCM) {
        int sumOfTime = 0;
        sumOfTime = taskList.stream().map((t)
                -> LCM / t.getPeriod() * t.getExecutionTime())
                .reduce(sumOfTime, Integer::sum);
        return sumOfTime;
    }
}
