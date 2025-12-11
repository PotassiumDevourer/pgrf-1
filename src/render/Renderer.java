package render;

import solid.Solid;
import rasterize.LineRasterizer;
import transforms.Col;
import transforms.Mat4;
import transforms.Point3D;
import transforms.Vec3D;

import java.awt.*;
import java.util.List;

public class Renderer {
    private LineRasterizer lineRasterizer;
    private int width, height;
    private Mat4 view,proj;


    public void renderSolid(Solid solid) {
        for (int i = 0; i < solid.getIb().size() ; i+= 2) {
            int indexA = solid.getIb().get(i);
            int indexB = solid.getIb().get(i + 1);
            Point3D a = solid.getVb().get(indexA);
            Point3D b = solid.getVb().get(indexB);
            a = a.mul(solid.getModel());
            b = b.mul(solid.getModel());
            a = a.mul(view);
            b = b.mul(view);
            a = a.mul(proj);
            b = b.mul(proj);
            a = a.mul(1/ a.getW());
            b = b.mul(1/b.getW());
            Vec3D vecA = transformToWindow(a);
            Vec3D vecB = transformToWindow(b);

            int endColor = solid.isSelected() ? 0x00FF00 : solid.getCb().get(indexA).getRGB();
            lineRasterizer.rasterize(
                    (int)Math.round(vecA.getX()),
                    (int)Math.round(vecA.getY()),
                    (int)Math.round(vecB.getX()),
                    (int)Math.round(vecB.getY()),
                    endColor
            );
        }
    }

    public Renderer(LineRasterizer lineRasterizer, int width, int height, Mat4 view, Mat4 proj) {
        this.lineRasterizer = lineRasterizer;
        this.width = width;
        this.height = height;
        this.view = view;
        this.proj = proj;
    }

    private Vec3D transformToWindow(Point3D p) {
        return new Vec3D(p).mul(new Vec3D(1, -1, 1))
                .add(new Vec3D(1, 1, 0))
                .mul(new Vec3D((double) (width-1)/2, (double)(height - 1)/2, 1));
    }

    public void setView(Mat4 view) {
        this.view = view;
    }

    public void setProj(Mat4 proj) {
        this.proj = proj;
    }

    public Mat4 getView() {
        return view;
    }

    public Mat4 getProj() {
        return proj;
    }

    public void renderSolids(List<Solid> solids) {
        if(solids == null)
            return;
        for (int i = 0; i < solids.size(); i++) {
            renderSolid(solids.get(i));
        }
    }
}
