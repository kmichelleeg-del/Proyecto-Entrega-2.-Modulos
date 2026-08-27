public class ReglaInactividad {

    private int maximoFaltas;
    private int diasSinParticipar;
    private int plazoConfirmacion;

    public ReglaInactividad(int maximoFaltas, int diasSinParticipar, int plazoConfirmacion) {

        this.maximoFaltas = maximoFaltas;
        this.diasSinParticipar = diasSinParticipar;
        this.plazoConfirmacion = plazoConfirmacion;
    }

    public int getMaximoFaltas() {
        return maximoFaltas;
    }

    public void setMaximoFaltas(int maximoFaltas) {
        this.maximoFaltas = maximoFaltas;
    }

    public int getDiasSinParticipar() {
        return diasSinParticipar;
    }

    public void setDiasSinParticipar(int diasSinParticipar) {
        this.diasSinParticipar = diasSinParticipar;
    }

    public int getPlazoConfirmacion() {
        return plazoConfirmacion;
    }

    public void setPlazoConfirmacion(int plazoConfirmacion) {
        this.plazoConfirmacion = plazoConfirmacion;
    }

    public boolean evaluar(Membresia membresia) {
        return false;
    }

    public void configurar() {
    }
}