/*
 *  Lince - Automatizacion de datos observacionales
 *  Copyright (C) 2010  Brais Gabin Moreira
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package lince.registro;

import java.awt.GridLayout;
import java.util.*;
import javax.swing.JPanel;

import lince.modelo.InstrumentoObservacional.Categoria;
import lince.modelo.InstrumentoObservacional.Criterio;
import lince.modelo.InstrumentoObservacional.InstrumentoObservacional;
import lince.modelo.InstrumentoObservacional.NodoInformacion;

/**
 *
 * @author Brais
 */
public class PanelAnadirRegistro extends JPanel implements Observer {

    private List<PanelRegistrarCriterio> registrarCriterios;
    private PanelRegistrarDatosMixto registrarDatosMixtos;

    public PanelAnadirRegistro() {
        InstrumentoObservacional.addObservador(this);
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridLayout(0, 1));

        registrarCriterios = new ArrayList<PanelRegistrarCriterio>();
        Criterio criterios[] = InstrumentoObservacional.getInstance().getCriterios();

        for (Criterio criterio : criterios) {
            if (!criterio.empty()) {
                PanelRegistrarCriterio registrarCriterio = new PanelRegistrarCriterio(criterio);
                registrarCriterios.add(registrarCriterio);
                add(registrarCriterio);
            }
        }

        NodoInformacion mixtos[] = InstrumentoObservacional.getInstance().getDatosMixtos();

        if (mixtos.length > 0) {
            registrarDatosMixtos = new PanelRegistrarDatosMixto(mixtos);
            add(registrarDatosMixtos);
        }
    }

    public Map<Criterio, Categoria> getSelected() {
        Map<Criterio, Categoria> criterios = new HashMap<Criterio, Categoria>();
        for (PanelRegistrarCriterio registrarCriterio : registrarCriterios) {
            Criterio criterio = registrarCriterio.getCriterio();
            Categoria categoria = registrarCriterio.getSelectedCategoria();
            if (categoria != null) {
                criterios.put(criterio, categoria);
            }
        }
        return criterios;
    }

    public Map<NodoInformacion, String> getDatosMixtos() {
        return registrarDatosMixtos.getDatosMixtosYValores();
    }

    public void cleanSelected() {
        for (PanelRegistrarCriterio registrarCriterio : registrarCriterios) {
            registrarCriterio.cleanSelected();
        }
    }

    public void seudoCleanSelected() {
        for (PanelRegistrarCriterio registrarCriterio : registrarCriterios) {
            registrarCriterio.seudoCleanSelected();
        }
    }

    public void update(Observable o, Object arg) {
        removeAll();
        initComponents();
        repaint();
        validate();
    }
}
