using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Destroyer : MonoBehaviour
{
    public static GameObject clickedGameObject;
    // Start is called before the first frame update
    void Start()
    {
        
    }

    // Update is called once per frame
    void Update()
    {
        if (Input.GetMouseButtonDown(1))
        {
            Ray ray = Camera.main.ScreenPointToRay(Input.mousePosition);
            RaycastHit hit = new RaycastHit();

            //マウスクリックした場所からRayを飛ばし、オブジェクトがあればtrue 
            if (Physics.Raycast(ray.origin, ray.direction, out hit, Mathf.Infinity))
            {
                clickedGameObject = hit.collider.gameObject.transform.root.gameObject;
                if (clickedGameObject.CompareTag("Plan"))
                {
                    Destroy(clickedGameObject);
                    Debug.Log("destroyed: " + clickedGameObject);
                }
            }
        }
    }
}
