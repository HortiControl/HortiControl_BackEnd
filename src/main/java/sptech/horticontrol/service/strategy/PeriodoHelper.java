package sptech.horticontrol.service.strategy;

import java.time.DayOfWeek;
import java.time.Month;


public final class PeriodoHelper {

    private PeriodoHelper() {
        throw new UnsupportedOperationException("Classe utilitária — não instanciar");
    }

    public static String traduzirMes(Month mes) {
        return switch (mes) {
            case JANUARY   -> "Jan";
            case FEBRUARY  -> "Fev";
            case MARCH     -> "Mar";
            case APRIL     -> "Abr";
            case MAY       -> "Mai";
            case JUNE      -> "Jun";
            case JULY      -> "Jul";
            case AUGUST    -> "Ago";
            case SEPTEMBER -> "Set";
            case OCTOBER   -> "Out";
            case NOVEMBER  -> "Nov";
            case DECEMBER  -> "Dez";
        };
    }

    public static String traduzirDiaSemana(DayOfWeek dia) {
        return switch (dia) {
            case MONDAY    -> "Seg";
            case TUESDAY   -> "Ter";
            case WEDNESDAY -> "Qua";
            case THURSDAY  -> "Qui";
            case FRIDAY    -> "Sex";
            case SATURDAY  -> "Sáb";
            case SUNDAY    -> "Dom";
        };
    }
}
