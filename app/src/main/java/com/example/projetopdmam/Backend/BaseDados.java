package com.example.projetopdmam.Backend;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.projetopdmam.Modelos.Caso;
import com.example.projetopdmam.Modelos.Estacionamento;
import com.example.projetopdmam.Modelos.Lugar;
import com.example.projetopdmam.Modelos.Utilizador;

import java.util.ArrayList;
import java.util.List;

public class BaseDados extends SQLiteOpenHelper {

    private static final int VERSAO_BASE_DADOS = 1;
    private static final String NOME_BASE_DADOS = "bd_cparking";

    /* TABELA UTILIZADORES */
    private static final String TABELA_UTILIZADORES = "tb_utilizadores";
    private static final String UTILIZADORES_ID = "Id";
    private static final String UTILIZADORES_NOME = "Nome";
    private static final String UTILIZADORES_CODIGO_POSTAL = "CodigoPostal";
    private static final String UTILIZADORES_TELEMOVEL = "Telemovel";
    private static final String UTILIZADORES_NIF = "NIF";
    private static final String UTILIZADORES_EMAIL = "Email";
    private static final String UTILIZADORES_PASSWORD = "Password";
    private static final String UTILIZADORES_ISACTIVE = "IsActive";

    /* TABELA LUGARES */
    private static final String TABELA_LUGARES = "tb_lugares";
    private static final String LUGARES_ID = "Id";
    private static final String LUGARES_CODIGO = "Codigo";
    private static final String LUGARES_ANDAR = "Andar";
    private static final String LUGARES_ISACTIVE = "IsActive";

    /* TABELA ESTACIONAMENTOS */
    private static final String TABELA_ESTACIONAMENTOS = "tb_estacionamentos";
    private static final String ESTACIONAMENTOS_ID = "Id";
    private static final String ESTACIONAMENTOS_UTILIZADORID = "UtilizadorId";
    private static final String ESTACIONAMENTOS_LUGARID = "LugarId";
    private static final String ESTACIONAMENTOS_DATAENTRADA = "DataEntrada";
    private static final String ESTACIONAMENTOS_DATASAIDA = "DataSaida";
    private static final String ESTACIONAMENTOS_LIVRE = "Livre";
    private static final String ESTACIONAMENTOS_ISACTIVE = "IsActive";

    /* TABELA CASOS */
    private static final String TABELA_CASOS = "tb_casos";
    private static final String CASOS_ID = "Id";
    private static final String CASOS_ESTACIONAMENTOID = "EstacionamentoId";
    private static final String CASOS_TITULO = "Titulo";
    private static final String CASOS_DESCRICAO = "Descricao";
    private static final String CASOS_FOTOGRAFIA = "Fotografia";
    private static final String CASOS_ISACTIVE = "IsActive";

    public BaseDados(Context context) {
        super(context, NOME_BASE_DADOS, null, VERSAO_BASE_DADOS);
    }

    @Override
    public void onCreate(SQLiteDatabase bd) {

        String QUERY_CREATE_TABLE_UTILIZADORES = "CREATE TABLE " + TABELA_UTILIZADORES + "("
                + UTILIZADORES_ID + " INTEGER PRIMARY KEY, " + UTILIZADORES_NOME + " TEXT, "
                + UTILIZADORES_CODIGO_POSTAL + " TEXT, " + UTILIZADORES_TELEMOVEL + " TEXT, "
                + UTILIZADORES_NIF + " TEXT, " + UTILIZADORES_EMAIL + " TEXT, "
                + UTILIZADORES_PASSWORD + " TEXT, "
                + UTILIZADORES_ISACTIVE + " INTEGER)";
        String QUERY_CREATE_TABLE_LUGARES = "CREATE TABLE " + TABELA_LUGARES + "("
                + LUGARES_ID + " INTEGER PRIMARY KEY, " + LUGARES_CODIGO + " TEXT, "
                + LUGARES_ANDAR + " TEXT, " + LUGARES_ISACTIVE + " INTEGER)";
        String QUERY_CREATE_TABLE_ESTACIONAMENTOS = "CREATE TABLE " + TABELA_ESTACIONAMENTOS + "("
                + ESTACIONAMENTOS_ID + " INTEGER PRIMARY KEY, " + ESTACIONAMENTOS_UTILIZADORID + " INTEGER, "
                + ESTACIONAMENTOS_LUGARID + " INTEGER, " + ESTACIONAMENTOS_DATAENTRADA + " TEXT, "
                + ESTACIONAMENTOS_DATASAIDA + " TEXT, " + ESTACIONAMENTOS_LIVRE + " INTEGER,"
                + ESTACIONAMENTOS_ISACTIVE + " INTEGER)";
        String QUERY_CREATE_TABLE_CASOS = "CREATE TABLE " + TABELA_CASOS + "("
                + CASOS_ID + " INTEGER PRIMARY KEY, " + CASOS_ESTACIONAMENTOID + " INTEGER, "
                + CASOS_TITULO + " TEXT, " + CASOS_DESCRICAO + " TEXT, "
                + CASOS_FOTOGRAFIA + " TEXT, " + CASOS_ISACTIVE + " INTEGER)";
        bd.execSQL(QUERY_CREATE_TABLE_UTILIZADORES);
        bd.execSQL(QUERY_CREATE_TABLE_LUGARES);
        bd.execSQL(QUERY_CREATE_TABLE_ESTACIONAMENTOS);
        bd.execSQL(QUERY_CREATE_TABLE_CASOS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase bd, int i, int i1) {

    }

    /*CRUD UTILIZADORES*/

    //Adicionar Utilizador
    public void loginLocal(Utilizador utilizador){

        SQLiteDatabase bd = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(UTILIZADORES_ID, utilizador.getId());
        values.put(UTILIZADORES_NOME, utilizador.getNome());
        values.put(UTILIZADORES_CODIGO_POSTAL, utilizador.getCodigoPostal());
        values.put(UTILIZADORES_TELEMOVEL, utilizador.getTelemovel());
        values.put(UTILIZADORES_NIF, utilizador.getNIF());
        values.put(UTILIZADORES_EMAIL, utilizador.getEmail());
        values.put(UTILIZADORES_PASSWORD, utilizador.getPassword());
        values.put(UTILIZADORES_ISACTIVE, utilizador.isActive() ? 1 : 0);

        bd.insert(TABELA_UTILIZADORES, null, values);
        bd.close();
    }

    //Eliminar Utilizador
    public void logoutLocal(){
        int Id = getLoggedInUser().getId();
        int lugarId = getLugarLocal().getId();
        int estacionamentoId = getEstacionamentoADecorrer().getId();
        int casoId = getCasoPorIdEstacionamento(estacionamentoId).getId();
        SQLiteDatabase bd = this.getWritableDatabase();

        bd.delete(TABELA_UTILIZADORES, UTILIZADORES_ID + " = ?", new String[] {String.valueOf(Id)});
        bd.delete(TABELA_ESTACIONAMENTOS, ESTACIONAMENTOS_ID + " = ?", new String[] {String.valueOf(estacionamentoId)});
        bd.delete(TABELA_LUGARES, LUGARES_ID + " = ?", new String[] {String.valueOf(lugarId)});
        bd.delete(TABELA_CASOS, CASOS_ID + " = ?", new String[] {String.valueOf(casoId)});

        bd.close();
    }

    //Get do utilizador
    public Utilizador getLoggedInUser(){
        SQLiteDatabase bd = this.getWritableDatabase();

        Utilizador utilizador = new Utilizador();
        String query = "SELECT * FROM " + TABELA_UTILIZADORES;

        Cursor cursor = bd.rawQuery(query, null);

        if(cursor.moveToFirst()){
            utilizador.setId(Integer.parseInt(cursor.getString(0)));
            utilizador.setNome(cursor.getString(1));
            utilizador.setCodigoPostal(cursor.getString(2));
            utilizador.setTelemovel(cursor.getString(3));
            utilizador.setNIF(cursor.getString(4));
            utilizador.setEmail(cursor.getString(5));
            utilizador.setPassword(cursor.getString(6));;
            utilizador.setActive(Integer.parseInt(cursor.getString(7)) == 1);
        }

        bd.close();

        return utilizador;
    }

    //Editar utilizador
    public void editarUtilizador(Utilizador utilizador){
        SQLiteDatabase bd = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UTILIZADORES_NOME, utilizador.getNome());
        values.put(UTILIZADORES_CODIGO_POSTAL, utilizador.getCodigoPostal());
        values.put(UTILIZADORES_TELEMOVEL, utilizador.getTelemovel());
        values.put(UTILIZADORES_NIF, utilizador.getNIF());
        values.put(UTILIZADORES_EMAIL, utilizador.getEmail());
        values.put(UTILIZADORES_PASSWORD, utilizador.getPassword());
        values.put(UTILIZADORES_ISACTIVE, utilizador.isActive() ? 1 : 0);

        bd.update(TABELA_UTILIZADORES, values, UTILIZADORES_ID + " = ?",
                new String[] {String.valueOf(utilizador.getId())});

        bd.close();
    }

    //Get de todos os utilizadores
    public List<Utilizador> getTodosUtilizadores(){
        List<Utilizador> listaUtilizadores = new ArrayList<Utilizador>();

        String query = "SELECT * FROM " + TABELA_UTILIZADORES;

        SQLiteDatabase bd = this.getWritableDatabase();
        Cursor cursor = bd.rawQuery(query, null);

        if(cursor.moveToFirst()){
            do {
                Utilizador utilizador = new Utilizador();
                utilizador.setId(Integer.parseInt(cursor.getString(0)));
                utilizador.setNome(cursor.getString(1));
                utilizador.setCodigoPostal(cursor.getString(2));
                utilizador.setTelemovel(cursor.getString(3));
                utilizador.setNIF(cursor.getString(4));
                utilizador.setEmail(cursor.getString(5));
                utilizador.setPassword(cursor.getString(6));;
                utilizador.setActive(Integer.parseInt(cursor.getString(7)) == 1);

                listaUtilizadores.add(utilizador);
            } while (cursor.moveToNext());
        }

        bd.close();

        return listaUtilizadores;
    }


    /*CRUD LUGARES*/

    //Adicionar Lugar
    public void adicionarLugar(Lugar lugar){
        SQLiteDatabase bd = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(LUGARES_ID, lugar.getId());
        values.put(LUGARES_CODIGO, lugar.getCodigo());
        values.put(LUGARES_ANDAR, lugar.getAndar());
        values.put(LUGARES_ISACTIVE, lugar.isActive() ? 1 : 0);

        bd.insert(TABELA_LUGARES, null, values);
        bd.close();
    }

    //Eliminar Lugar
    public void eliminarLugar(int Id){
        SQLiteDatabase bd = this.getWritableDatabase();

        bd.delete(TABELA_LUGARES, LUGARES_ID + " = ?", new String[] {String.valueOf(Id)});

        bd.close();
    }

    //Get lugar por Id
    public Lugar getLugarPorId(int Id){
        try{
            SQLiteDatabase bd = this.getReadableDatabase();

            Cursor cursor = bd.query(TABELA_LUGARES, new String[] {LUGARES_ID, LUGARES_CODIGO,
                            LUGARES_ANDAR, LUGARES_ISACTIVE}, LUGARES_ID + " = ?",
                    new String[] {String.valueOf(Id)}, null, null, null, null);
            if(cursor != null) {
                cursor.moveToFirst();
            }
            Lugar lugar = new Lugar(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2),
                    Integer.parseInt(cursor.getString(7)) == 1);

            bd.close();

            return lugar;
        }catch (Exception e){
            return new Lugar();
        }
    }

    public Lugar getLugarLocal(){
        SQLiteDatabase bd = this.getWritableDatabase();

        Lugar lugar = new Lugar();
        String query = "SELECT * FROM " + TABELA_LUGARES;

        Cursor cursor = bd.rawQuery(query, null);

        if(cursor.moveToFirst()){
            lugar.setId(Integer.parseInt(cursor.getString(0)));
            lugar.setCodigo(cursor.getString(1));
            lugar.setAndar(cursor.getString(2));
            lugar.setActive(Integer.parseInt(cursor.getString(3)) == 1);
        }

        bd.close();

        return lugar;
    }

    //Editar lugar
    public void editarLugar(Lugar lugar){
        SQLiteDatabase bd = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LUGARES_CODIGO, lugar.getCodigo());
        values.put(LUGARES_ANDAR, lugar.getAndar());
        values.put(LUGARES_ISACTIVE, lugar.isActive() ? 1 : 0);

        bd.update(TABELA_LUGARES, values, LUGARES_ID + " = ?",
                new String[] {String.valueOf(lugar.getId())});

        bd.close();
    }

    //Get de todos os lugares
    public List<Lugar> getTodosLugares(){
        List<Lugar> listaLugares = new ArrayList<Lugar>();

        String query = "SELECT * FROM " + TABELA_LUGARES;

        SQLiteDatabase bd = this.getWritableDatabase();
        Cursor cursor = bd.rawQuery(query, null);

        if(cursor.moveToFirst()){
            do {
                Lugar lugar = new Lugar();
                lugar.setId(Integer.parseInt(cursor.getString(0)));
                lugar.setCodigo(cursor.getString(1));
                lugar.setAndar(cursor.getString(2));
                lugar.setActive(Integer.parseInt(cursor.getString(7)) == 1);
                listaLugares.add(lugar);
            } while (cursor.moveToNext());
        }

        bd.close();

        return listaLugares;
    }


    /*CRUD ESTACIONAMENTOS*/

    //Adicionar estacionamento
    public void comecarEstacionamentoLocal(Estacionamento estacionamento){
        SQLiteDatabase bd = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(ESTACIONAMENTOS_ID, estacionamento.getId());
        values.put(ESTACIONAMENTOS_UTILIZADORID, estacionamento.getUtilizadorId());
        values.put(ESTACIONAMENTOS_LUGARID, estacionamento.getLugarId());
        values.put(ESTACIONAMENTOS_DATAENTRADA, estacionamento.getDataEntrada());
        values.put(ESTACIONAMENTOS_DATASAIDA, estacionamento.getDataSaida());
        values.put(ESTACIONAMENTOS_LIVRE, estacionamento.isEstacionamentoLivre() ? 1 : 0);
        values.put(ESTACIONAMENTOS_ISACTIVE, estacionamento.isActive() ? 1 : 0);

        bd.insert(TABELA_ESTACIONAMENTOS, null, values);
        bd.close();
    }

    //Eliminar estacionamento
    public void acabarEstacionamentoLocal(){
        Estacionamento estacionamentoADecorrer = getEstacionamentoADecorrer();
        int Id = estacionamentoADecorrer.getId();

        SQLiteDatabase bd = this.getWritableDatabase();

        bd.delete(TABELA_ESTACIONAMENTOS, ESTACIONAMENTOS_ID + " = ?", new String[] {String.valueOf(Id)});

        bd.close();

        Lugar lugar = getLugarLocal();

        if(lugar.isActive()){
            eliminarLugar(estacionamentoADecorrer.getLugarId());
        }

        eliminarTodosOsCasos();

    }

    //Get do estacionamento
    public Estacionamento getEstacionamentoADecorrer(){
        SQLiteDatabase bd = this.getWritableDatabase();

        Estacionamento estacionamento = new Estacionamento();
        String query = "SELECT * FROM " + TABELA_ESTACIONAMENTOS;

        Cursor cursor = bd.rawQuery(query, null);

        if(cursor.moveToFirst()){
            estacionamento.setId(Integer.parseInt(cursor.getString(0)));
            estacionamento.setUtilizadorId(Integer.parseInt(cursor.getString(1)));
            estacionamento.setLugarId(Integer.parseInt(cursor.getString(2)));
            estacionamento.setDataEntrada(cursor.getString(3));
            estacionamento.setDataSaida(cursor.getString(4));
            estacionamento.setEstacionamentoLivre(Integer.parseInt(cursor.getString(5)) == 1);
            estacionamento.setActive(Integer.parseInt(cursor.getString(6)) == 1);
        }

        bd.close();

        return estacionamento;
    }

    //Editar estacionamento
    public void editarEstacionamento(Estacionamento estacionamento){
        SQLiteDatabase bd = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ESTACIONAMENTOS_UTILIZADORID, estacionamento.getUtilizadorId());
        values.put(ESTACIONAMENTOS_LUGARID, estacionamento.getLugarId());
        values.put(ESTACIONAMENTOS_DATAENTRADA, estacionamento.getDataEntrada());
        values.put(ESTACIONAMENTOS_DATASAIDA, estacionamento.getDataSaida());
        values.put(ESTACIONAMENTOS_LIVRE, estacionamento.isEstacionamentoLivre() ? 1 : 0);
        values.put(ESTACIONAMENTOS_ISACTIVE, estacionamento.isActive() ? 1 : 0);

        bd.update(TABELA_ESTACIONAMENTOS, values, ESTACIONAMENTOS_ID + " = ?",
                new String[] {String.valueOf(estacionamento.getId())});

        bd.close();
    }

    //Get de todos os estacionamentos
    public List<Estacionamento> getTodosEstacionamentos(){
        List<Estacionamento> listaEstacionamentos = new ArrayList<Estacionamento>();

        String query = "SELECT * FROM " + TABELA_ESTACIONAMENTOS;

        SQLiteDatabase bd = this.getWritableDatabase();
        Cursor cursor = bd.rawQuery(query, null);

        if(cursor.moveToFirst()){
            do {
                Estacionamento estacionamento = new Estacionamento();
                estacionamento.setId(Integer.parseInt(cursor.getString(0)));
                estacionamento.setUtilizadorId(Integer.parseInt(cursor.getString(1)));
                estacionamento.setLugarId(Integer.parseInt(cursor.getString(2)));
                estacionamento.setDataEntrada(cursor.getString(3));
                estacionamento.setDataSaida(cursor.getString(4));
                estacionamento.setEstacionamentoLivre(Integer.parseInt(cursor.getString(5)) == 1);
                estacionamento.setActive(Integer.parseInt(cursor.getString(6)) == 1);

                listaEstacionamentos.add(estacionamento);
            } while (cursor.moveToNext());
        }

        bd.close();

        return listaEstacionamentos;
    }


    /*CRUD CASOS*/

    //Adicionar Caso
    public void adicionarCaso(Caso caso){
        SQLiteDatabase bd = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(CASOS_ID, caso.getId());
        values.put(CASOS_ESTACIONAMENTOID, caso.getEstacionamentoId());
        values.put(CASOS_TITULO, caso.getTitulo());
        values.put(CASOS_DESCRICAO, caso.getDescricao());
        values.put(CASOS_FOTOGRAFIA, caso.getFotografia());
        values.put(CASOS_ISACTIVE, caso.isActive() ? 1 : 0);

        bd.insert(TABELA_CASOS, null, values);
        bd.close();
    }

    //Eliminar Caso
    public void eliminarCaso(int Id){
        SQLiteDatabase bd = this.getWritableDatabase();

        bd.delete(TABELA_CASOS, CASOS_ID + " = ?", new String[] {String.valueOf(Id)});

        bd.close();
    }

    //ELIMINAR TODOS OS CASOS
    public void eliminarTodosOsCasos(){
        SQLiteDatabase bd = this.getWritableDatabase();

        String query = "DELETE FROM " + TABELA_CASOS;

        bd.execSQL(query);

        bd.close();
    }

    //Get Caso por Id
    public Caso getCasoPorId(int Id){
        SQLiteDatabase bd = this.getReadableDatabase();

        Cursor cursor = bd.query(TABELA_CASOS, new String[] {CASOS_ID, CASOS_ESTACIONAMENTOID, CASOS_TITULO,
                        CASOS_DESCRICAO, CASOS_FOTOGRAFIA, CASOS_ISACTIVE}, CASOS_ID + " = ?",
                new String[] {String.valueOf(Id)}, null, null, null, null);
        if(cursor != null) {
            cursor.moveToFirst();
        }
        Caso caso = new Caso(Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(1)), cursor.getString(2), cursor.getString(3),
                cursor.getString(4), Integer.parseInt(cursor.getString(5)) == 1);

        return caso;
    }

    //Editar Caso
    public void editarCaso(Caso caso){
        SQLiteDatabase bd = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CASOS_ESTACIONAMENTOID, caso.getEstacionamentoId());
        values.put(CASOS_TITULO, caso.getTitulo());
        values.put(CASOS_DESCRICAO, caso.getDescricao());
        values.put(CASOS_FOTOGRAFIA, caso.getFotografia());
        values.put(CASOS_ISACTIVE, caso.isActive() ? 1 : 0);

        bd.update(TABELA_CASOS, values, CASOS_ID + " = ?",
                new String[] {String.valueOf(caso.getId())});

        bd.close();
    }

    //Get de todos os Casos
    public List<Caso> getTodosCasos(){
        List<Caso> listaCasos = new ArrayList<Caso>();

        String query = "SELECT * FROM " + TABELA_CASOS;

        SQLiteDatabase bd = this.getWritableDatabase();
        Cursor cursor = bd.rawQuery(query, null);

        if(cursor.moveToFirst()){
            do {
                Caso caso = new Caso();
                caso.setId(Integer.parseInt(cursor.getString(0)));
                caso.setEstacionamentoId(Integer.parseInt(cursor.getString(1)));
                caso.setTitulo(cursor.getString(2));
                caso.setDescricao(cursor.getString(3));
                caso.setFotografia(cursor.getString(4));
                caso.setActive(Integer.parseInt(cursor.getString(5)) == 1);
                listaCasos.add(caso);
            } while (cursor.moveToNext());
        }

        bd.close();

        return listaCasos;
    }

    //Get do Caso pelo Estacionamento Id
    public Caso getCasoPorIdEstacionamento(int Id){
        SQLiteDatabase bd = this.getWritableDatabase();

        Caso caso = new Caso();
        String query = "SELECT * FROM " + TABELA_CASOS;

        Cursor cursor = bd.rawQuery(query, null);

        if(cursor.moveToFirst()){
            caso.setId(Integer.parseInt(cursor.getString(0)));
            caso.setEstacionamentoId(Integer.parseInt(cursor.getString(1)));
            caso.setTitulo(cursor.getString(2));
            caso.setDescricao(cursor.getString(3));
            caso.setFotografia(cursor.getString(4));
            caso.setActive(Integer.parseInt(cursor.getString(5)) == 1);
        }

        bd.close();

        return caso;
    }

}
