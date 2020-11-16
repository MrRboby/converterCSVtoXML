package converter.controller;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Контроллер обработки исключений
 */
@ControllerAdvice
public final class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Обрабаботка исключения загрузки файла, размер которого превышающего допустимый предел
     *
     * @param exception обрабатываемое исключение
     * @param model     модель
     * @return шаблон страницы с ошибкой
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleError(MaxUploadSizeExceededException exception, Model model) {
        model.addAttribute("error", "размер файла превысил допустимый предел (128 Кб)");
        logger.warn("Файл превысил допустимый размер ({})", exception.getMessage());
        return "error";
    }

    /**
     * Обрабаботка исключения загрузки пустого файла
     *
     * @param exception обрабатываемое исключение
     * @param model     модель
     * @return шаблон страницы с ошибкой
     */
    @ExceptionHandler(FileNotFoundException.class)
    public String handleError(FileNotFoundException exception, Model model) {
        model.addAttribute("error", "файл не загружен или пустой");
        logger.warn("Файл не загружен или загружен пустой ({})", exception.getMessage());
        return "error";
    }

    /**
     * Обрабаботка исключения при создании XML файла
     *
     * @param exception обрабатываемое исключение
     * @param model     модель
     * @return шаблон страницы с ошибкой
     */
    @ExceptionHandler(JAXBException.class)
    public String handleError(JAXBException exception, Model model) {
        model.addAttribute("error", "ошибка при создании XML файла");
        logger.warn("Ошибка при создании XML файла ({})", exception.getMessage());
        return "error";
    }

    /**
     * Обрабаботка исключения при загрузке CSV файла с пустым необходимым полем
     *
     * @param exception обрабатываемое исключение
     * @param model     модель
     * @return шаблон страницы с ошибкой
     */
    @ExceptionHandler(CsvRequiredFieldEmptyException.class)
    public String handleError(CsvRequiredFieldEmptyException exception, Model model) {
        model.addAttribute("error",
                String.format("CSV файл содержит пустые ячейки в строке %d", exception.getLineNumber()));
        logger.warn("Некорректный CSV файл ({})", exception.getMessage());
        return "error";
    }

    /**
     * Обработка исключения при загрузке CSV файла с некорректными значениями полей
     *
     * @param exception обрабатываемое исключение
     * @param model     модель
     * @return шаблон страницы с ошибкой
     */
    @ExceptionHandler(CsvDataTypeMismatchException.class)
    public String handleError(CsvDataTypeMismatchException exception, Model model) {
        model.addAttribute("error", exception.getMessage());
        logger.warn("Некорректный CSV файл ({})", exception.getMessage());
        return "error";
    }

    /**
     * Обрабаботка исключения при некорректном выборе типа сортировки
     *
     * @param exception обрабатываемое исключение
     * @param model     модель
     * @return шаблон страницы с ошибкой
     */
    @ExceptionHandler(NoSuchElementException.class)
    public String handleError(NoSuchElementException exception, Model model) {
        model.addAttribute("error", "некорректно выбран тип сортировки");
        logger.warn("Тип сортировки выбран некорректно ({})", exception.getMessage());
        return "error";
    }

    /**
     * Обрабаботка исключения при некорректном вводе значений для фильтрации
     *
     * @param exception обрабатываемое исключение
     * @param model     модель
     * @return шаблон страницы с ошибкой
     */
    @ExceptionHandler(org.springframework.validation.BindException.class)
    public String handleError(org.springframework.validation.BindException exception, Model model) {
        model.addAttribute("error", "некорректно введены значения для фильтрации");
        logger.warn("Данные для фильтрации введены некорректно ({})", exception.getMessage());
        return "error";
    }

    /**
     * Обрабаботка исключения, вызванного потоками данных
     *
     * @param exception обрабатываемое исключение
     * @param model     модель
     * @return шаблон страницы с ошибкой
     */
    @ExceptionHandler(IOException.class)
    public String handleError(IOException exception, Model model) {
        model.addAttribute("error", exception.getLocalizedMessage());
        logger.warn("Ошибка ввода-вывода ({})", exception.getMessage());
        return "error";
    }
}
