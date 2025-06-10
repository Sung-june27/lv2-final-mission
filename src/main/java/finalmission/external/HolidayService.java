package finalmission.external;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HolidayService {

    private final HolidayRestClient holidayRestClient;

    public boolean isHoliday(LocalDate date) {
        List<Integer> holidays = holidayRestClient.getHolidayByMonth(date);
        return holidays.contains(date.getDayOfMonth());
    }
}
