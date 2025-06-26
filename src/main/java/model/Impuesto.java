package model;

import java.time.LocalDate;

public class Impuesto {
    private long sticker;
    private LocalDate fechaMovimiento;
    private LocalDate fechaRecaudo;
    private String tipoHorario;
    private long nroId;
    private long nroForm;
    private long valor;

    public Impuesto(long sticker, LocalDate fechaMovimiento, LocalDate fechaRecaudo,
                    String tipoHorario, long nroId, long nroForm, long valor) {
        this.sticker = sticker;
        this.fechaMovimiento = fechaMovimiento;
        this.fechaRecaudo = fechaRecaudo;
        this.tipoHorario = tipoHorario;
        this.nroId = nroId;
        this.nroForm = nroForm;
        this.valor = valor;
    }

    public long getSticker() {
        return sticker;
    }

    public LocalDate getFechaMovimiento() {
        return fechaMovimiento;
    }

    public LocalDate getFechaRecaudo() {
        return fechaRecaudo;
    }

    public String getTipoHorario() {
        return tipoHorario;
    }

    public long getNroId() {
        return nroId;
    }

    public long getNroForm() {
        return nroForm;
    }

    public long getValor() {
        return valor;
    }
}
