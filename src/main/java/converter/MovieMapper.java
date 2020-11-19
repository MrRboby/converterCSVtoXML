package converter;

import converter.model.MovieInput;
import converter.model.MovieOutput;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MovieMapper {
    MovieOutput movieInputToOutput(MovieInput movieInput);
}
