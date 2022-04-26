package Lotus;

public abstract class Scene
{
    protected Camera camera;
    public Scene()
    {

    }

    public void init()
    {

    }
    public abstract void Update(float dt);
}
