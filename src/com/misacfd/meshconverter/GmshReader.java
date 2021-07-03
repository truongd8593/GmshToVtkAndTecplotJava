package com.misacfd.meshconverter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class GmshReader {
    private long nbNode;
    private long nbElMsh;
    private long nbElm;
    private String fName;
    private List<Point> coordNodes;
    private List<NodeIdent> idNodes;
    private List<NodeIdentMsh> idNodesMsh;

    private static GmshReader instance = new GmshReader();

    public static GmshReader getInstance() {
        return instance;
    }

    private GmshReader() {

    }

    public long getNbNode() {
        return nbNode;
    }

    public void setNbNode(long nbNode) {
        this.nbNode = nbNode;
    }

    public long getNbElMsh() {
        return nbElMsh;
    }

    public void setNbElMsh(long nbElMsh) {
        this.nbElMsh = nbElMsh;
    }

    public long getNbElm() {
        return nbElm;
    }

    public void setNbElm(long nbElm) {
        this.nbElm = nbElm;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public List<Point> getCoordNodes() {
        return coordNodes;
    }

    public void setCoordNodes(List<Point> coordNodes) {
        this.coordNodes = coordNodes;
    }

    public List<NodeIdent> getIdNodes() {
        return idNodes;
    }

    public void setIdNodes(List<NodeIdent> idNodes) {
        this.idNodes = idNodes;
    }

    public List<NodeIdentMsh> getIdNodesMsh() {
        return idNodesMsh;
    }

    public void setIdNodesMsh(List<NodeIdentMsh> idNodesMsh) {
        this.idNodesMsh = idNodesMsh;
    }

    public void readMesh() throws IOException {
        setfName("input.dat");

        File file = new File(getfName());
        BufferedReader br = new BufferedReader(new FileReader(file));

        String st;
        while ((st = br.readLine()) != null) {
            System.out.println(String.format("Mesh file to be processed : %s", st));
        }

        StringBuilder mshFile = new StringBuilder(st).append(".msh");

        setfName(mshFile.toString());

        file = new File(getfName());
        br = new BufferedReader(new FileReader(file));

        String line;
        String[] tokens;

        line = br.readLine();
        line = br.readLine();
        line = br.readLine();
        line = br.readLine();

        line = br.readLine();
        nbNode = Integer.parseUnsignedInt(line);

        for (long i = 0; i < nbNode; i++) {
            line = br.readLine();
            tokens = line.split(" ");

            long ident = Integer.parseUnsignedInt(tokens[0]);
            double x = Double.parseDouble(tokens[1]);
            double y = Double.parseDouble(tokens[2]);
            double z = Double.parseDouble(tokens[3]);

            Point p =
                    new Point.Builder()
                            .setIdent(ident)
                            .setX(x)
                            .setY(y)
                            .setZ(z)
                            .build();

            coordNodes.add(p);
        }

        line = br.readLine();
        line = br.readLine();

        line = br.readLine();
        nbElMsh = Integer.parseUnsignedInt(line);

        setNbElm(0);

        for (long i = 0; i < nbElMsh; i++) {
            line = br.readLine();
            tokens = line.split(" ");

            NodeIdentMsh nodeIdentMsh =
                    new NodeIdentMsh.Builder()
                            .setIdent(Integer.parseUnsignedInt(tokens[0]))
                            .setElemTyp(Integer.parseUnsignedInt(tokens[1]))
                            .setNbTags(Integer.parseUnsignedInt(tokens[2]))
                            .setTag1(Integer.parseUnsignedInt(tokens[3]))
                            .setTag2(Integer.parseUnsignedInt(tokens[4]))
                            .build();

            idNodesMsh.add(nodeIdentMsh);

            int elemTyp = idNodesMsh.get((int) i).getElemTyp();

            switch (elemTyp) {
                case 1:  // 2 - node line.
                case 15: // 1-node point.
                case 27: // boundary 5-node edge.
                    break;
                case 3:  // 4-node quadrangle.
                case 37: // 5-node edge quadrangle.
                    nbElm = nbElm + 1;
                    break;
                default:
                    throw new RuntimeException("Element type is not suppoted. Comming soon !");
            }
        }

    }

    public void constructIdNodes() {
        for (int i = 0; i < nbElMsh; i++) {
            int elmTyp = idNodesMsh.get(i).getElemTyp();

            switch (elmTyp) {
                case 3:
                case 37:
                    NodeIdent node =
                            new NodeIdent.Builder()
                            .setIdNode(idNodesMsh.get(i).getIdNode())
                            .build();

                    idNodes.add(node);
                    break;
                default:
                    // do nothing
            }
        }
    }
}
