package com.lifeos.service;

import com.lifeos.entity.RepeatType;
import com.lifeos.entity.Task;
import com.lifeos.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class RecurringTaskService {

    private final TaskRepository taskRepository;
    private final RecurrenceCalculator recurrenceCalculator;

    public RecurringTaskService(
            TaskRepository taskRepository,
            RecurrenceCalculator recurrenceCalculator
    ) {
        this.taskRepository = taskRepository;
        this.recurrenceCalculator = recurrenceCalculator;
    }

    /**
     * Processes every active recurring master task.
     */
    @Transactional
    public void processRecurringTasks() {

        List<Task> masterTasks =
                taskRepository.findByRecurrenceMasterTrueAndRecurrenceActiveTrue();

        for (Task masterTask : masterTasks) {
            processMasterTask(masterTask);
        }
    }

    /**
     * Processes a single master recurring task.
     */
    private void processMasterTask(Task masterTask) {

        LocalDate today = LocalDate.now();

        LocalDate lastGeneratedDate = masterTask.getLastGeneratedDate();

        if (lastGeneratedDate == null) {
            lastGeneratedDate = masterTask.getTaskDate();
        }

        LocalDate nextDate = recurrenceCalculator.calculateNextDate(
                lastGeneratedDate,
                masterTask.getRepeatType()
        );

        while (!nextDate.isAfter(today)) {

            boolean alreadyExists = taskRepository.existsByMasterTaskIdAndTaskDate(
                    masterTask.getId(),
                    nextDate
            );

            if (!alreadyExists) {

                createOccurrence(masterTask, nextDate);

            }

            masterTask.setLastGeneratedDate(nextDate);

            nextDate = recurrenceCalculator.calculateNextDate(
                    nextDate,
                    masterTask.getRepeatType()
            );
        }

        taskRepository.save(masterTask);
    }

    /**
     * Creates a normal task occurrence from a master recurring task.
     */
    private void createOccurrence(
            Task masterTask,
            LocalDate occurrenceDate
    ) {

        Task occurrence = new Task();

        occurrence.setTaskName(masterTask.getTaskName());
        occurrence.setDescription(masterTask.getDescription());

        occurrence.setTaskDate(occurrenceDate);
        occurrence.setTaskTime(masterTask.getTaskTime());

        occurrence.setPriority(masterTask.getPriority());

        occurrence.setCompleted(false);

        /*
         * Generated occurrences never generate more tasks.
         */
        occurrence.setRepeatType(RepeatType.NEVER);
        occurrence.setRecurrenceMaster(false);
        occurrence.setRecurrenceActive(false);

        occurrence.setMasterTaskId(masterTask.getId());

        occurrence.setLastGeneratedDate(null);

        occurrence.setUser(masterTask.getUser());

        taskRepository.save(occurrence);
    }

}