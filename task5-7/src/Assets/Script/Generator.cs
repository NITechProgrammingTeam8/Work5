using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Generator : MonoBehaviour
{
    // Start is called before the first frame update
    void Start()
    {
        
    }

    // Update is called once per frame
    void Update()
    {
        if (Input.GetKeyDown(KeyCode.Alpha1))
        {
            GameObject obj = (GameObject)Resources.Load("Cube");
            Instantiate(obj, obj.transform.position, Quaternion.identity);
        }
        if (Input.GetKeyDown(KeyCode.Alpha2))
        {
            GameObject obj = (GameObject)Resources.Load("Torus");
            Instantiate(obj, obj.transform.position, Quaternion.identity);
        }
        if (Input.GetKeyDown(KeyCode.Alpha3))
        {
            GameObject obj = (GameObject)Resources.Load("Sphere");
            Instantiate(obj, obj.transform.position, Quaternion.identity);
        }
    }
}
