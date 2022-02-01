package com.example.projetopdmsam.Backend;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import com.example.projetopdmsam.Modelos.Caso;
import com.example.projetopdmsam.Modelos.Inspecao;
import com.example.projetopdmsam.Modelos.Obra;
import com.example.projetopdmsam.Modelos.Utilizador;

import java.util.ArrayList;
import java.util.List;

public class BaseDados extends SQLiteOpenHelper {

    private static final int VERSAO_BASE_DADOS = 1;
    private static final String NOME_BASE_DADOS = "bd_renergy";

    /* TABELA UTILIZADORES */
    private static final String TABELA_UTILIZADORES = "tb_utilizadores";
    private static final String UTILIZADORES_ID = "Id";
    private static final String UTILIZADORES_NOME = "Nome";
    private static final String UTILIZADORES_USERNAME = "Username";
    private static final String UTILIZADORES_PASSWORD = "Password";
    private static final String UTILIZADORES_EMAIL = "Email";
    private static final String UTILIZADORES_TELEMOVEL = "Telemovel";
    private static final String UTILIZADORES_ISACTIVE = "IsActive";

    /* TABELA OBRAS */
    private static final String TABELA_OBRAS = "tb_obras";
    private static final String OBRAS_ID = "Id";
    private static final String OBRAS_NOME = "Nome";
    private static final String OBRAS_DESCRICAO = "Descricao";
    private static final String OBRAS_MORADA = "Morada";
    private static final String OBRAS_CODIGOPOSTAL = "CodigoPostal";
    private static final String OBRAS_LOCALIDADE = "Localidade";
    private static final String OBRAS_PAIS = "Pais";
    private static final String OBRAS_DATAINICIO = "DataInicio";
    private static final String OBRAS_RESPONSAVEL = "Responsavel";
    private static final String OBRAS_ISACTIVE = "IsActive";

    /* TABELA INSPECOES */
    private static final String TABELA_INSPECOES = "tb_inspecoes";
    private static final String INSPECOES_ID = "Id";
    private static final String INSPECOES_DATAINICIO = "DataInicio";
    private static final String INSPECOES_DATAFIM = "DataFim";
    private static final String INSPECOES_ISFINISHED = "IsFinished";
    private static final String INSPECOES_INSPETORID = "InspetorId";
    private static final String INSPECOES_OBRAID = "ObraId";
    private static final String INSPECOES_ISACTIVE = "IsActive";

    /* TABELA CASOS */
    private static final String TABELA_CASOS = "tb_casos";
    private static final String CASOS_ID = "Id";
    private static final String CASOS_TITULO = "Titulo";
    private static final String CASOS_DESCRICAO = "Descricao";
    private static final String CASOS_IMAGEM = "Imagem";
    private static final String CASOS_INSPECAOID = "InspecaoId";

    public BaseDados(Context context) {
        super(context, NOME_BASE_DADOS, null, VERSAO_BASE_DADOS);
    }

    @Override
    public void onCreate(SQLiteDatabase bd) {

        String QUERY_CREATE_TABLE_UTILIZADORES = "CREATE TABLE " + TABELA_UTILIZADORES + "("
                + UTILIZADORES_ID + " INTEGER PRIMARY KEY, " + UTILIZADORES_NOME + " TEXT, "
                + UTILIZADORES_USERNAME + " TEXT, " + UTILIZADORES_PASSWORD + " TEXT, "
                + UTILIZADORES_EMAIL + " TEXT, " + UTILIZADORES_TELEMOVEL + " TEXT,"
                + UTILIZADORES_ISACTIVE + " INTEGER)";
        String QUERY_CREATE_TABLE_OBRAS = "CREATE TABLE " + TABELA_OBRAS + "("
                + OBRAS_ID + " INTEGER PRIMARY KEY, " + OBRAS_NOME + " TEXT, "
                + OBRAS_DESCRICAO + " TEXT, " + OBRAS_MORADA + " TEXT, "
                + OBRAS_CODIGOPOSTAL + " TEXT, " + OBRAS_LOCALIDADE + " TEXT,"
                + OBRAS_PAIS + " TEXT, " + OBRAS_DATAINICIO + " TEXT, "
                + OBRAS_RESPONSAVEL + " TEXT, " + OBRAS_ISACTIVE + " INTEGER)";
        String QUERY_CREATE_TABLE_INSPECOES = "CREATE TABLE " + TABELA_INSPECOES + "("
                + INSPECOES_ID + " INTEGER PRIMARY KEY, " + INSPECOES_DATAINICIO + " TEXT, "
                + INSPECOES_DATAFIM + " TEXT, " + INSPECOES_ISFINISHED + " INTEGER, "
                + INSPECOES_INSPETORID + " TEXT, " + INSPECOES_OBRAID + " TEXT,"
                + INSPECOES_ISACTIVE + " INTEGER)";
        String QUERY_CREATE_TABLE_CASOS = "CREATE TABLE " + TABELA_CASOS + "("
                + CASOS_ID + " INTEGER PRIMARY KEY, " + CASOS_TITULO + " TEXT, "
                + CASOS_DESCRICAO + " TEXT, " + CASOS_IMAGEM + " TEXT, "
                + CASOS_INSPECAOID + " INTEGER)";
        bd.execSQL(QUERY_CREATE_TABLE_UTILIZADORES);
        bd.execSQL(QUERY_CREATE_TABLE_OBRAS);
        bd.execSQL(QUERY_CREATE_TABLE_INSPECOES);
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
        values.put(UTILIZADORES_USERNAME, utilizador.getUsername());
        values.put(UTILIZADORES_PASSWORD, utilizador.getPassword());
        values.put(UTILIZADORES_EMAIL, utilizador.getEmail());
        values.put(UTILIZADORES_TELEMOVEL, utilizador.getTelemovel());
        values.put(UTILIZADORES_ISACTIVE, utilizador.isActive() ? 1 : 0);

        bd.insert(TABELA_UTILIZADORES, null, values);
        bd.close();
    }

    //Eliminar Utilizador
    public void logoutLocal(){
        int Id = getLoggedInUser().getId();

        SQLiteDatabase bd = this.getWritableDatabase();

        bd.delete(TABELA_UTILIZADORES, UTILIZADORES_ID + " = ?", new String[] {String.valueOf(Id)});

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
            utilizador.setUsername(cursor.getString(2));
            utilizador.setPassword(cursor.getString(3));
            utilizador.setEmail(cursor.getString(4));
            utilizador.setTelemovel(cursor.getString(5));
            utilizador.setActive(Integer.parseInt(cursor.getString(6)) == 1);
        }

        bd.close();

        return utilizador;
    }

    //Editar utilizador
    public void editarUtilizador(Utilizador utilizador){
        SQLiteDatabase bd = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UTILIZADORES_NOME, utilizador.getNome());
        values.put(UTILIZADORES_USERNAME, utilizador.getUsername());
        values.put(UTILIZADORES_PASSWORD, utilizador.getPassword());
        values.put(UTILIZADORES_EMAIL, utilizador.getEmail());
        values.put(UTILIZADORES_TELEMOVEL, utilizador.getTelemovel());
        values.put(UTILIZADORES_ISACTIVE, utilizador.isActive());

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
                utilizador.setUsername(cursor.getString(2));
                utilizador.setPassword(cursor.getString(3));
                utilizador.setEmail(cursor.getString(4));
                utilizador.setTelemovel(cursor.getString(5));
                utilizador.setActive(Integer.parseInt(cursor.getString(6)) == 1);

                listaUtilizadores.add(utilizador);
            } while (cursor.moveToNext());
        }

        bd.close();

        return listaUtilizadores;
    }


    /*CRUD OBRAS*/

    //Adicionar Obra
    public void adicionarObra(Obra obra){
        SQLiteDatabase bd = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(OBRAS_ID, obra.getId());
        values.put(OBRAS_NOME, obra.getNome());
        values.put(OBRAS_DESCRICAO, obra.getDescricao());
        values.put(OBRAS_MORADA, obra.getMorada());
        values.put(OBRAS_CODIGOPOSTAL, obra.getCodigoPostal());
        values.put(OBRAS_LOCALIDADE, obra.getLocalidade());
        values.put(OBRAS_PAIS, obra.getPais());
        values.put(OBRAS_DATAINICIO, obra.getDataInicio());
        values.put(OBRAS_RESPONSAVEL, obra.getResponsavel());
        values.put(OBRAS_ISACTIVE, obra.isActive() ? 1 : 0);

        bd.insert(TABELA_OBRAS, null, values);
        bd.close();
    }

    //Eliminar Obra
    public void eliminarObra(int Id){
        SQLiteDatabase bd = this.getWritableDatabase();

        bd.delete(TABELA_OBRAS, OBRAS_ID + " = ?", new String[] {String.valueOf(Id)});

        bd.close();
    }

    //Get obra por Id
    public Obra getObraPorId(int Id){
        try{
            SQLiteDatabase bd = this.getReadableDatabase();

            Cursor cursor = bd.query(TABELA_OBRAS, new String[] {OBRAS_ID, OBRAS_NOME,
                            OBRAS_DESCRICAO, OBRAS_MORADA, OBRAS_CODIGOPOSTAL,
                            OBRAS_LOCALIDADE, OBRAS_PAIS, OBRAS_DATAINICIO,
                            OBRAS_RESPONSAVEL, OBRAS_ISACTIVE}, OBRAS_ID + " = ?",
                    new String[] {String.valueOf(Id)}, null, null, null, null);
            if(cursor != null) {
                cursor.moveToFirst();
            }
            Obra obra = new Obra(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6),
                    cursor.getString(7), cursor.getString(8), Integer.parseInt(cursor.getString(9)) == 1);

            return obra;
        }catch (Exception e){
            return new Obra();
        }
    }

    //Editar obra
    public void editarObra(Obra obra){
        SQLiteDatabase bd = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(OBRAS_NOME, obra.getNome());
        values.put(OBRAS_DESCRICAO, obra.getDescricao());
        values.put(OBRAS_MORADA, obra.getMorada());
        values.put(OBRAS_CODIGOPOSTAL, obra.getCodigoPostal());
        values.put(OBRAS_LOCALIDADE, obra.getLocalidade());
        values.put(OBRAS_PAIS, obra.getPais());
        values.put(OBRAS_DATAINICIO, obra.getDataInicio());
        values.put(OBRAS_RESPONSAVEL, obra.getResponsavel());
        values.put(OBRAS_ISACTIVE, obra.isActive() ? 1 : 0);

        bd.update(TABELA_OBRAS, values, OBRAS_ID + " = ?",
                new String[] {String.valueOf(obra.getId())});

        bd.close();
    }

    //Get de todas as obras
    public List<Obra> getTodasObras(){
        List<Obra> listaObras = new ArrayList<Obra>();

        String query = "SELECT * FROM " + TABELA_OBRAS;

        SQLiteDatabase bd = this.getWritableDatabase();
        Cursor cursor = bd.rawQuery(query, null);

        if(cursor.moveToFirst()){
            do {
                Obra obra = new Obra();
                obra.setId(Integer.parseInt(cursor.getString(0)));
                obra.setNome(cursor.getString(1));
                obra.setDescricao(cursor.getString(2));
                obra.setMorada(cursor.getString(3));
                obra.setCodigoPostal(cursor.getString(4));
                obra.setLocalidade(cursor.getString(5));
                obra.setPais(cursor.getString(5));
                obra.setDataInicio(cursor.getString(5));
                obra.setResponsavel(cursor.getString(5));
                obra.setActive(Integer.parseInt(cursor.getString(6)) == 1);
                listaObras.add(obra);
            } while (cursor.moveToNext());
        }

        bd.close();

        return listaObras;
    }


    /*CRUD INSPEÇÕES*/

    //Adicionar Inspeção
    public void comecarInspecaoLocal(Inspecao inspecao){
        SQLiteDatabase bd = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(INSPECOES_ID, inspecao.getId());
        values.put(INSPECOES_DATAINICIO, inspecao.getDataInicio());
        values.put(INSPECOES_DATAFIM, inspecao.getDataFim());
        values.put(INSPECOES_ISFINISHED, inspecao.isFinished() ? 1 : 0);
        values.put(INSPECOES_INSPETORID, inspecao.getInspetorId());
        values.put(INSPECOES_OBRAID, inspecao.getObraId());
        values.put(INSPECOES_ISACTIVE, inspecao.isActive() ? 1 : 0);

        bd.insert(TABELA_INSPECOES, null, values);
        bd.close();
    }

    //Eliminar Inspecao
    public void acabarInspecaoLocal(){
        Inspecao inspecaoADecorrer = getInspecaoADecorrer();
        int Id = inspecaoADecorrer.getId();

        SQLiteDatabase bd = this.getWritableDatabase();

        bd.delete(TABELA_INSPECOES, INSPECOES_ID + " = ?", new String[] {String.valueOf(Id)});

        bd.close();

        Obra obra = getObraPorId(inspecaoADecorrer.getObraId());

        if(obra.isActive()){
            eliminarObra(inspecaoADecorrer.getObraId());
        }

        eliminarTodosOsCasos();

    }

    //Get da inspecao
    public Inspecao getInspecaoADecorrer(){
        SQLiteDatabase bd = this.getWritableDatabase();

        Inspecao inspecao = new Inspecao();
        String query = "SELECT * FROM " + TABELA_INSPECOES;

        Cursor cursor = bd.rawQuery(query, null);

        if(cursor.moveToFirst()){
            inspecao.setId(Integer.parseInt(cursor.getString(0)));
            inspecao.setDataInicio(cursor.getString(1));
            inspecao.setDataFim(cursor.getString(2));
            inspecao.setFinished(Integer.parseInt(cursor.getString(3)) == 1);
            inspecao.setInspetorId(Integer.parseInt(cursor.getString(4)));
            inspecao.setObraId(Integer.parseInt(cursor.getString(5)));
            inspecao.setActive(Integer.parseInt(cursor.getString(6)) == 1);
        }

        bd.close();

        return inspecao;
    }

    //Editar inspecao
    public void editarInspecao(Inspecao inspecao){
        SQLiteDatabase bd = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(INSPECOES_DATAINICIO, inspecao.getDataInicio());
        values.put(INSPECOES_DATAFIM, inspecao.getDataFim());
        values.put(INSPECOES_ISFINISHED, inspecao.isFinished() ? 1 : 0);
        values.put(INSPECOES_INSPETORID, inspecao.getInspetorId());
        values.put(INSPECOES_OBRAID, inspecao.getObraId());
        values.put(INSPECOES_ISACTIVE, inspecao.isActive() ? 1 : 0);

        bd.update(TABELA_INSPECOES, values, INSPECOES_ID + " = ?",
                new String[] {String.valueOf(inspecao.getId())});

        bd.close();
    }

    //Get de todas as inspecoes
    public List<Inspecao> getTodasInspecoes(){
        List<Inspecao> listaInspecoes = new ArrayList<Inspecao>();

        String query = "SELECT * FROM " + TABELA_INSPECOES;

        SQLiteDatabase bd = this.getWritableDatabase();
        Cursor cursor = bd.rawQuery(query, null);

        if(cursor.moveToFirst()){
            do {
                Inspecao inspecao = new Inspecao();
                inspecao.setId(Integer.parseInt(cursor.getString(0)));
                inspecao.setDataInicio(cursor.getString(1));
                inspecao.setDataFim(cursor.getString(2));
                inspecao.setFinished(Integer.parseInt(cursor.getString(3)) == 1);
                inspecao.setInspetorId(Integer.parseInt(cursor.getString(4)));
                inspecao.setObraId(Integer.parseInt(cursor.getString(5)));
                inspecao.setActive(Integer.parseInt(cursor.getString(6)) == 1);

                listaInspecoes.add(inspecao);
            } while (cursor.moveToNext());
        }

        bd.close();

        return listaInspecoes;
    }


    /*CRUD CASOS*/

    //Adicionar Caso
    public void adicionarCaso(Caso caso){
        SQLiteDatabase bd = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(CASOS_ID, caso.getId());
        values.put(CASOS_TITULO, caso.getTitulo());
        values.put(CASOS_DESCRICAO, caso.getDescricao());
        values.put(CASOS_IMAGEM, caso.getImagem());
        values.put(CASOS_INSPECAOID, caso.getInspecaoId());

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

        Cursor cursor = bd.query(TABELA_CASOS, new String[] {CASOS_ID, CASOS_TITULO,
                        CASOS_DESCRICAO, CASOS_IMAGEM, CASOS_INSPECAOID}, CASOS_ID + " = ?",
                new String[] {String.valueOf(Id)}, null, null, null, null);
        if(cursor != null) {
            cursor.moveToFirst();
        }
        Caso caso = new Caso(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2),
                cursor.getString(3), Integer.parseInt(cursor.getString(4)));

        return caso;
    }

    //Editar Caso
    public void editarCaso(Caso caso){
        SQLiteDatabase bd = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CASOS_TITULO, caso.getTitulo());
        values.put(CASOS_DESCRICAO, caso.getDescricao());
        values.put(CASOS_IMAGEM, caso.getImagem());
        values.put(CASOS_INSPECAOID, caso.getInspecaoId());

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
                caso.setTitulo(cursor.getString(1));
                caso.setDescricao(cursor.getString(2));
                caso.setImagem(cursor.getString(3));
                caso.setInspecaoId(Integer.parseInt(cursor.getString(4)));
                listaCasos.add(caso);
            } while (cursor.moveToNext());
        }

        bd.close();

        return listaCasos;
    }

    //Get de todos os Casos por Inspeção Id
    public List<Caso> getCasosPorIdInspecao(int Id){
        List<Caso> listaCasos = new ArrayList<Caso>();

        String query = "SELECT * FROM " + TABELA_CASOS + " WHERE " + CASOS_INSPECAOID + " = " + Id;

        SQLiteDatabase bd = this.getWritableDatabase();
        Cursor cursor = bd.rawQuery(query, null);

        if(cursor.moveToFirst()){
            do {
                Caso caso = new Caso();
                caso.setId(Integer.parseInt(cursor.getString(0)));
                caso.setTitulo(cursor.getString(1));
                caso.setDescricao(cursor.getString(2));
                caso.setImagem(cursor.getString(3));
                caso.setInspecaoId(Integer.parseInt(cursor.getString(4)));
                listaCasos.add(caso);
            } while (cursor.moveToNext());
        }

        bd.close();

        return listaCasos;
    }

}
